## 简介

Elasticsearch 是一个基于 Lucene 库的搜索引擎。

可以使用 elasticsearch-head 和 Postman 创建索引库和增删改查。

**ES 与 Solr 的对比**

都是基于 Lucene。

Solr 利用 ZooKeeper 进行分布式管理，而 Elasticsearch 自身带有分布式协调管理功能。

Solr 支持更多格式的数据，而 Elasticsearch 仅支持 JSON 文件格式。

Solr 官方提供的功能更多，而 Elasticsearch 本身更注重于核心功能，高级功能多有第三方插件提供。

Solr 在传统的搜索应用中表现好于 Elasticsearch，但在处理实时搜索应用时效率明显低于 Elasticsearch。



## 1.分布式架构原理

核心思想就是在多台机器上启动多个 ES 进程实例，组成了一个 ES 集群。
ES 中存储数据的基本单位是索引 index，一个 index 差不多就是相当于是 MySQL 里的一张表。
index -> type -> mapping -> document -> field。
一个 index 里可以有多个 type，每个 type 的字段都是差不多的，但是有一些略微的差别。很多情况下，一个 index 里可能就一个 type。
mapping 就是这个 type 的表结构定义。一条 document 就代表了 MySQL 中某个表里的一行，每个 document 有多个 field，每个 field 就代表了这个 document 中的一个字段的值。

索引拆分成多个 shard，每个 shard 存储部分数据。好处：一是支持横向扩展，二是提高性能（所有的操作都会在多台机器上并行分布式执行，提高了吞吐量和性能）。
shard 的数据实际是有多个备份，每个 shard 都有一个 primary shard，负责写入数据，但是还有几个 replica shard。primary shard 写入数据之后，会将数据同步到其他几个 replica shard 上去。通过这个 replica 的方案，每个 shard 的数据都有多个备份，如果某个机器宕机了，还有别的数据副本在别的机器上，实现高可用。
es 集群多个节点，会自动选举一个节点为 master 节点负责管理，要是 master 节点宕机了，那么会重新选举一个节点为 master 节点。
如果是非 master 节点宕机了，那么会由 master 节点，让那个宕机节点上的 primary shard 的身份转移到其他机器上的 replica shard。接着你要是修复了那个宕机机器，重启了之后，master 节点会控制将缺失的 replica shard 分配过去，同步后续修改的数据。如果宕机的机器修复了，修复后的节点是 replica shard。              



## 2.读写搜索数据

ES 写数据过程：
客户端选择一个 node 发送请求过去，这个 node 就是协调节点 coordinating node。
coordinating node 对 document 进行路由，将请求转发给对应的 node（有 primary shard）。
实际的 node 上的 primary shard 处理请求，然后将数据同步到 replica node。
coordinating node 如果发现 primary node 和所有 replica node 都搞定之后，就返回响应结果给客户端。

ES 读数据过程：
可以通过 doc id 来查询，会根据 doc id 进行 hash，判断出来当时把 doc id 分配到了哪个 shard 上面去，从那个 shard 去查询。
客户端发送请求到任意一个 node，成为 coordinate node。
coordinate node 对 doc id 进行哈希路由，将请求转发到对应的 node，此时会使用随机轮询算法 round-robin，在 primary shard 以及其所有 replica 中随机选择一个，让读请求负载均衡。
接收请求的 node 返回 document 给 coordinate node。
coordinate node 返回 document 给客户端。

ES 搜索数据过程：
ES 最强大的是做全文检索。
客户端发送请求到一个 coordinate node。
协调节点将搜索请求转发到所有的 shard 对应的 primary shard 或 replica shard，都可以。
query phase：每个 shard 将自己的搜索结果（其实就是一些 doc id）返回给协调节点，由协调节点进行数据的合并、排序、分页等操作，产出最终结果。
fetch phase：接着由协调节点根据 doc id 去各个节点上拉取实际的 document 数据，最终返回给客户端。

写请求是写入 primary shard，然后同步给所有的 replica shard；读请求可以从 primary shard 或 replica shard 读取，采用的是随机轮询算法。

写数据底层原理：
数据先写入内存 buffer，然后每隔 1s，将数据 refresh 到 os cache，到了 os cache 数据就能被搜索到（所以我们才说 ES 从写入到能被搜索到，中间有 1s 的延迟）。
每隔 5s，将数据写入 translog 文件（这样如果机器宕机，内存数据全没，最多会有 5s 的数据丢失），translog 大到一定程度，或者默认每隔 30mins，会触发 commit 操作，将缓冲区的数据都 flush 到 segment file 磁盘文件中。
数据写入 segment file 之后，同时就建立好了倒排索引。

删除/更新数据底层原理：
删除操作，commit 的时候会生成一个 .del 文件，里面将某个 doc 标识为 deleted 状态，那么搜索的时候根据 .del 文件就知道这个 doc 是否被删除了。
更新操作，就是将原来的 doc 标识为 deleted 状态，然后新写入一条数据。
buffer 每 refresh 一次，就会产生一个 segment file，所以默认情况下是 1 秒钟一个 segment file，这样下来 segment file 会越来越多，此时会定期执行 merge。
每次 merge 的时候，会将多个 segment file 合并成一个，同时这里会将标识为 deleted 的 doc 给物理删除掉，然后将新的 segment file 写入磁盘，这里会写一个 commit point，标识所有新的 segment file，然后打开 segment file 供搜索使用，同时删除旧的 segment file。

Lucene：一个 JAR 包，里面包含了封装好的各种建立倒排索引的算法代码。
通过 Lucene，我们可以将已有的数据建立索引，Lucene 会在本地磁盘上面，给我们组织索引的数据结构。

#### 倒排索引

是关键词到文档 ID 的映射，每个关键词都对应着一系列的文件，这些文件中都出现了关键词。

在搜索引擎中，每个文档都有一个对应的文档 ID，文档内容被表示为一系列关键词的集合。例如，文档 1 经过分词，提取了 20 个关键词，每个关键词都会记录它在文档中出现的次数和出现位置。

另外，实用的倒排索引还可以记录更多的信息，比如文档频率信息，表示在文档集合中有多少个文档包含某个单词。

有了倒排索引，搜索引擎可以很方便地响应用户的查询。

倒排索引中的词项根据字典顺序升序排列。

**创建索引过程**

获取文档 -> 构建文档对象 -> 分析文档（分词，手动或使用分析器）-> 创建索引。

基于关键词 term 创建索引。索引库中包括关键词、Document 和两者之间的对应关系。

传统方法根据文件找到文件内容，顺序扫描，数据量大，索引慢。

**查询索引过程**

用户查询接口 -> 把关键词映射为一个查询对象 -> 执行查询（Query，包括 term 和 range，或者 ID） -> 渲染结果（分页等）



可以使用 Luke 查看索引库的信息。

中文分析器 IKAnalyze，是 ES 的一个插件。

QueryParser：先分词，再根据分词结果查询。 

设置分页和高亮

Spring Data ElasticSearch：applicationContext 中配置 elastic 客户对象，包扫描器来扫描 DAO 和 elasticsearchTemplate 对象。

使用 NativeSearchQuery 查询



## 3.ES 在数据量很大的情况下如何提高查询效率？

性能优化的杀手锏 filesystem cache：你往 ES 里写的数据，实际上都写到磁盘文件里去了，查询的时候，操作系统会将磁盘文件里的数据自动缓存到 filesystem cache 里面去。
ES 的搜索引擎严重依赖于底层的 filesystem cache，你如果给 filesystem cache 更多的内存，尽量让内存可以容纳所有的 idx segment file 索引数据文件，那么你搜索的时候就基本都是走内存的，性能会非常高。
最佳的情况下，就是你的机器的内存，至少可以容纳你的总数据量的一半。在 ES 中就存少量用来搜索的那些索引，数据几乎全部走内存来搜索，性能非常之高。

数据预热：比较热的、经常会有人访问的数据，最好做一个专门的缓存预热子系统，就是对热数据每隔一段时间，就提前访问一下，让数据进入 filesystem cache 里面去。
冷热分离：类似于 MySQL 的水平拆分，将大量的访问很少、频率很低的数据单独写入一个索引中，然后热数据写入另外一个索引中，这样可以确保热数据在被预热之后，尽量都让他们留在 filesystem os cache 里，别让冷数据给冲刷掉。
document 模型设计：ES 里面的复杂的关联查询尽量别用，先在 Java 系统里就完成关联，将关联好的数据直接写入 ES 中。复杂的操作，尽量在 document 模型设计的时候，写入的时候就完成。
分页性能优化：不允许深度分页（默认深度分页性能很差，默认翻的越深，性能就越差），可以使用用 scroll api，只能一页一页翻，不能随意跳到任何一页的场景。
scroll 会一次性给你生成所有数据的一个快照，然后每次滑动向后翻页就是通过游标 scroll_id 移动，获取下一页。初始化时必须指定 scroll 参数，告诉 ES 要保存此次搜索的上下文多长时间，操作超时会失败。
也可以用 search_after，使用前一页的结果来帮助检索下一页的数据，只能一页页往后翻。初始化时，需要使用一个唯一值的字段作为 sort 字段。