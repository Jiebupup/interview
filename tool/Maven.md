## 简介

Maven 是一个 Java 软件项目管理及自动构建工具，由 Apache 软件基金会所提供。

基于项目对象模型 POM 概念，Maven 利用一个中央信息片断能管理一个项目的构建、报告和文档等步骤。

#### 作用

**依赖管理**

不再需要手动处理 Jar 依赖包，可以自动处理依赖关系。传统项目 JAR 包在项目中，Maven 项目 JAR 包在项目外的仓库中，减小了项目大小并且 JAR 包可以重用。

**一键构建**

构建工具可以自动化测试、编译、运行、打包、发布、安装、部署这些操作，从而为我们减少这些繁琐的工作。



## 1.命令

和底层的插件对应。

clean：清除 target 目录。

compile：编译 main 下。

test：编译 main + test。

package：指定打包方式（默认打成 Jar 包），会先编译 main + test。

install：test + package，还要把项目装到本地仓库。

deploy：发布。



VM Options：-DarchetypeCatalog=internal，下载过的插件可以重用。

dependencyManagement：锁定 JAR 包版本，但是没有导入 JAR 包的功能。

properties：一个 map，用来统一管理 JAR 包版本。

Maven 工程的拆分和组装：子模块一旦创建就可以使用父模块的所有资源。多个子模块之间互相依赖，需要将各自的 JAR 包放到本地仓库中。

传递依赖丢失：表现为 JAR 包导入不了，解决方案是再导入一次。



## 2.重要概念

**Ant、Maven 和 Gradle**

Ant 具有编译、测试和打包功能。

Maven 在 Ant 的功能基础上又新增了依赖管理功能。提供了 POM 文件来管理项目的构建。

Gradle 又在 Maven 的功能基础上新增了对 Groovy 语言的支持。Gradle 使用特定领域语言 DSL 来管理构建脚本，而不再使用 XML 这种标记性语言。因为项目如果庞大的话，XML 很容易就变得臃肿。

#### 仓库

仓库的搜索顺序为：本地仓库 -> 中央仓库 -> 远程仓库（镜像和私服）。

本地仓库用来存储项目的依赖库。

中央仓库是下载依赖库的默认位置。

远程仓库，因为并非所有的依赖库都在中央仓库，或者中央仓库访问速度很慢，远程仓库是中央仓库的补充。

#### POM

是一个 XML 文件，保存在项目根目录的 pom.xml 文件中。

[groupId, artifactId, version, packaging, classifier] 称为一个项目的坐标，其中 groupId、artifactId、version 必须定义，packaging 可选（默认为 Jar），classifier 不能直接定义的，需要结合插件使用。

groupId：项目组 Id，必须全球唯一。

artifactId：项目 Id，即项目名。

version：项目版本。

packaging：项目打包方式。

scope：默认 compile 全范围、test、provided、runtime、system。

**依赖原则**

依赖路径最短优先原则、声明顺序优先原则和覆写优先原则。

**解决依赖冲突**

找到 Maven 加载的 Jar 包版本，使用 mvn dependency:tree 查看依赖树，根据依赖原则来调整依赖在 POM 文件的声明顺序。