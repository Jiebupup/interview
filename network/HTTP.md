## 简介

超文本传输协议 HTTP，是一个客户端和服务端之间请求和应答的标准。通常使用 TCP 协议，服务器上默认端口为 80，URL:80 = URL。



## 1.HTTP 请求方法

**GET**

获取资源。

设计作为查询使用，典型应用是搜索引擎。

当前网络请求中，绝大部分使用的是 GET 方法。

**POST**

传输数据。

**PUT**

上传文件。

自身不带验证机制，任何人都可以上传文件，因此存在安全性问题。

HTTP/1.1 开始有 PUT 及后面的方法。

**DELETE**

与 PUT 功能相反。

**HEAD**

获取报文首部。

和 GET 方法类似。

主要用于确认 URL 的有效性以及资源更新的日期时间等。

**PATCH**

对资源进行部分修改。

PUT 也可以用于修改资源，但是只能完全替代原始资源。

**OPTIONS**

查询指定的 URL 能够支持的方法。

**CONNECT**

使用安全套接层 SSL 和传输层安全 TLS 协议把通信内容加密后经网络隧道传输。

**TRACE**

服务器会将通信路径返回给客户端。

通常不会使用 TRACE，并且它容易受到跨站追踪 XST 攻击。

#### GET 和 POST 的区别

**作用**

GET 用于获取资源，而 POST 用于传输实体主体。

**参数**

GET 和 POST 的请求都能使用额外的参数，但是 GET 的参数是以查询字符串出现在 URL 中，两者使用 ? 连接，而各个变量之间使用 & 连接。而 POST 的参数存储在请求 head（如公用信息像组织信息）& body 中。

GET 请求的数据会暴露在地址栏中。敏感数据还是应该使用 POST，但不能因为 POST 参数存储在实体主体中就认为它的安全性更高，因为照样可以通过一些抓包工具（如 Fiddler）查看。      

因为 URL 只支持 ASCII 码，因此 GET 的参数中如果存在中文等字符就需要先进行编码。POST 参数支持标准字符集。

GET 传输的数据要受到 URL 长度限制，而 POST 可以传输大量的数据（用于上传文件）。

**安全性**

GET 是安全的，而 POST 不是。POST 的目的是传送实体主体内容，这个内容可能是用户上传的表单数据，上传成功之后，服务器可能把这个数据存储到数据库中，因此状态也就发生了改变。

安全的 HTTP 方法不会改变服务器状态，也就是说它只是可读的。

安全的方法 GET、HEAD、OPTIONS。不安全的方法 POST、PUT、DELETE。

GET 在浏览器回退时是无害的，POST 会再次提交请求。

**幂等性**

GET 是幂等的，而 POST 不是。POST 如果调用多次，就会增加多行记录。

幂等的 HTTP 方法，同样的请求被执行一次与连续执行多次的效果是一样的，服务器的状态也是一样的。换句话说就是，幂等方法不应该具有副作用（统计用途除外）。

所有的安全方法也都是幂等的。在正确实现的条件下，PUT 和 DELETE 等方法都是幂等的。

**能否缓存**

GET 可缓存（会被浏览器主动缓存，保留在浏览器历史记录里或被 bookmark），而 POST 不可缓存（除非手动设置）。

**缓存的条件**

- 请求报文的 HTTP 方法本身是可缓存的，如 GET 和 HEAD。
- 响应报文的状态码是可缓存的，如 200, 203, 204, 206, 300, 301, 404, 405, 410, 414, 501。
- 响应报文的 Cache-Control 首部字段没有指定不进行缓存。

**XMLHttpRequest**

在使用 XMLHttpRequest 的 GET 方法时，浏览器会将 Header 和 Data 一起发送。而 POST 方法会先发送 Header 再发送 Data，但并不是所有浏览器都会这么做，例如火狐。

XMLHttpRequest 是一个 API，它为客户端提供了在客户端和服务器之间传输数据的功能。它提供了一个通过 URL 来获取数据的简单方式，并且不会使整个页面刷新。这使得网页只更新一部分页面而不会打扰到用户。XMLHttpRequest 在 AJAX 中被大量使用。

**效率**

GET 效率比 POST 高。

 

## 2.HTTP 状态码

#### 1XX 信息

接收的请求正在处理。

100 Continue：表明到目前为止都很正常，客户端可以继续发送请求或者忽略这个响应。

#### 2XX 成功

200 OK

201 Created：成功请求并创建了新的资源。

204 No Content：请求已经成功处理，但是返回的响应报文不包含实体的主体部分。一般在只需要从客户端往服务器发送信息，而不需要返回数据时使用。

206 Partial Content：表示客户端进行了范围请求，响应报文包含由 Content-Range 指定范围的实体内容。

#### 3XX 重定向

需要进行附加操作以完成请求。

301 Moved Permanently：永久性重定向。

302 Found：临时性重定向。

303 See Other：和 302 有着相同的功能，但是 303 明确要求客户端应该采用 GET 方法获取资源。虽然 HTTP 协议规定 301、302 状态下重定向时不允许把 POST 方法改成 GET 方法，但是大多数浏览器都会在 301、302 和 303 状态下的重定向把 POST 方法改成 GET 方法。

304 Not Modified：所请求的资源未修改，服务器返回此状态码时，不会返回任何资源。客户端通常会缓存访问过的资源，通过提供一个头信息指出客户端希望只返回在指定日期之后修改的资源。不满足请求报文首部包含的一些条件，例如：If-Match，If-Modified-Since，If-None-Match，If-Range，If-Unmodified-Since。

307 Temporary Redirect：临时重定向，与 302 的含义类似，但是 307 要求浏览器不会把重定向请求的 POST 方法改成 GET 方法。

#### 4XX 客户端错误

400 Bad Request：请求报文中存在语法错误。

401 Unauthorized：该状态码表示发送的请求需要有认证信息（BASIC 认证、DIGEST 认证）。如果之前已进行过一次请求，则表示用户认证失败。

403 Forbidden：请求被拒绝。

404 Not Found

405 Method Not Allowed

#### 5XX 服务器错误

500 Internal Server Error：服务器正在执行请求时发生错误。

502 Bad Gateway：作为网关或者代理工作的服务器尝试执行请求时，从远程服务器接收到了一个无效的响应。

503 Service Unavailable：服务器暂时处于超负载或正在进行停机维护，现在无法处理请求。

504 Gateway Time-out：充当网关或代理的服务器，未及时从远端服务器获取请求。



## 3.HTTP 请求响应报文结构

#### 请求

HTTP 请求报文由请求行、请求头部（多行）、空行和请求正文 4 个部分组成。

**请求行**

由请求方法、URL 和 HTTP 协议版本三部分组成，它们用空格分隔。

#### 响应

HTTP 响应报文由状态行、响应头部（多行）、空行和响应正文 4 个部分组成。

**状态行**

由 HTTP 协议版本、状态码、状态码描述三部分组成。

#### HTTP 首部

**通用首部字段**

Cache-Control：控制缓存的行为。max-age 属性。

Connection：控制不再转发给代理的首部字段、管理持久连接。

**请求首部字段**

Accept：用户代理可处理的媒体类型。

Authorization：Web 认证信息。

Host：请求资源所在服务器。web 代理高速缓存所需要的。

If-Modified-Since：比较资源的更新时间。

If-None-Match：比较实体标记 ETag。

If-Range：资源未更新时发送实体 Byte 的范围请求。

Max-Forwards：最大传输逐跳数。和 TRACE 方法配合。

Range：实体的字节范围请求。

Referer：对请求中 URI 的原始获取方。防盗链，统计工作，防御 CSRF。

User-Agent：HTTP 客户端程序的信息。浏览器兼容性问题。

**响应首部字段**

ETag：资源的匹配信息。

Location：令客户端重定向至指定 URI。

Server：HTTP 服务器的安装信息。

Vary：代理服务器缓存的管理信息。

**实体首部字段**

Content-Length：实体主体的大小。

Content-Type：响应正文媒体类型 MIME type。头部必须是 ASCII 码。

Expires：实体主体过期的日期时间。

Last-Modified：资源的最后修改日期时间。

**简单请求和非简单请求**

**简单请求同时满足的两个条件**

- 请求方法是 GET、POST、HEAD 三种之一。

- HTTP 头信息不超过右边这几个字段：Accept、Accept-Language、Content-Language、Last-Event-IDContent-Type，只限于三个值 application/x-www-form-urlencoded、multipart/form-data、text/plain。

凡是不同时满足这两个条件的，都属于非简单请求。

**浏览器处理简单请求和非简单请求的方式**

- 对于简单请求，浏览器会在头信息中增加 Origin 字段后直接发出，Origin 字段用来说明，本次请求来自的哪个源（协议+域名+端口）。如果服务器发现 Origin 指定的源不在许可范围内，服务器会返回一个正常的 HTTP 回应，浏览器取到回应之后发现回应的头信息中没有包含 Access-Control-Allow-Origin 字段，报错。如果服务器发现 Origin 指定的域名在许可范围内，服务器返回的响应会多出几个 Access-Control- 开头的头信息字段。

- 非简单请求是那种对服务器有特殊要求的请求，浏览器会在正式通信之前，发送一次 HTTP 预检 OPTIONS 请求，先询问服务器，当前网页所在的域名是否在服务器的许可名单之中，以及可以使用哪些 HTTP 请求方法和头信息字段。只有得到肯定答复，浏览器才会发出正式的 XMLHttpRequest 请求，否则报错。



## 4.应用

#### 连接管理

<img src="/Users/wangjie/Desktop/面试知识点/pic/http1.png" alt="http1" style="zoom: 50%;" />

**短连接与长连接**

当浏览器访问一个包含多张图片的 HTML 页面时，除了请求访问的 HTML 页面资源，还会请求图片资源。如果每进行一次 HTTP 通信就要新建一个 TCP 连接，那么开销会很大。

长连接只需要建立一次 TCP 连接就能进行多次 HTTP 通信（包括请求和响应，SSL 也可以使用之前）。

HTTP/1.0 默认是短连接的，如果需要使用长连接，则使用 Connection:Keep-Alive。可以在服务器软件（如 Apache）中设定 Keep-Alive 的保持时间。

从 HTTP/1.1 开始默认是长连接的，如果要断开连接，需要由客户端或者服务器端提出断开，使用 Connection:close。

**流水线 pipelining** 

默认情况下，HTTP 请求是按顺序发出的，下一个请求只有在当前请求收到响应之后才会被发出。由于受到网络延迟和带宽的限制，在下一个请求被发送到服务器之前，可能需要等待很长时间。

流水线是在同一条长连接上连续发出请求，而不用等待响应返回，这样可以减少延迟。

#### 缓存

**优点**

- 缓解服务器压力。

- 降低客户端获取资源的延迟：缓存通常位于内存中，读取缓存的速度更快。并且缓存服务器在地理位置上也有可能比源服务器来得近，例如浏览器缓存。

**实现方法**

代理服务器，客户端浏览器。

**Cache-Control**

HTTP/1.1 通过 Cache-Control 首部字段来控制缓存。

- no-store：禁止对请求或响应的任何一部分进行缓存。

- no-cache：规定缓存服务器需要先向源服务器验证缓存资源的有效性，只有当缓存资源有效时才能使用该缓存对客户端的请求进行响应。

- private：私有缓存，只能被单独用户使用，一般存储在用户浏览器中。

- public：公共缓存，可以被多个用户使用，一般存储在代理服务器中。

**缓存过期机制**

max-age 指令出现在请求报文，并且缓存资源的缓存时间小于该指令指定的时间，那么就能接受该缓存。

max-age 指令出现在响应报文，表示缓存资源在缓存服务器中保存的时间。

Expires 首部字段也可以用于告知缓存服务器该资源什么时候会过期。

在 HTTP/1.0 中，max-age 指令会被忽略掉。在 HTTP/1.1 中，会优先处理 max-age 指令。 

**Expires 和 max-age 的区别**

- Expires 在 HTTP/1.0 中已经定义。Cache-Control:max-age= 在 HTTP/1.1 中才有定义。

- Expires 指定一个绝对的过期时间（GMT 格式），可以是相对文件的最后访问时间 Atime 或者修改时间 MTime，这么做会导致客户端和服务器时间不同步，使 Expires 的配置出现问题。很容易在配置后忘记具体的过期时间，导致过期来临出现浪涌现象。

- max-age 指定的是从文档被访问后的存活时间，这个时间是个相对值，相对的是文档第一次被请求时服务器记录的 Request_time。

- Apache 中，max-age 是根据 Expires 的时间来计算出来的。

**缓存验证**

**ETag**

ETag 是资源的唯一标识，URL 不能唯一表示资源，例如 http://www.google.com/ 有中文和英文两个资源，只有 ETag 才能对这两个资源进行唯一标识。

可以将缓存资源的 ETag 值放入 If-None-Match 首部，服务器收到该请求后，判断缓存资源的 ETag 值和资源的最新 ETag 值是否一致，如果一致则表示缓存资源有效，返回 304 Not Modified。

**Last-Modified**

Last-Modified 首部字段也可以用于缓存验证，它包含在源服务器发送的响应报文中，指示源服务器对资源的最后修改时间。

但是它是一种弱校验器，因为只能精确到一秒，所以它通常作为 ETag 的备用方案。

如果响应首部字段里含有这个信息，客户端可以在后续的请求中带上 If-Modified-Since 来验证缓存。服务器只在所请求的资源在给定的日期时间之后对内容进行过修改的情况下才会将资源返回，状态码为 200 OK。

#### HTTP 首部相关

**内容协商**

通过内容协商返回最合适的内容，例如根据浏览器的默认语言选择返回中文界面还是英文界面。Accept-Language 和服务器返回 Vary: Accept-Language。

**内容编码**

内容编码将实体主体进行压缩，从而减少传输的数据量。Accept-Encoding &Content-Encoding。

**范围请求**

如果网络出现中断，服务器只发送了一部分数据，范围请求可以使得客户端只请求服务器未发送的那部分数据，从而避免服务器重新发送所有数据。Range&Accept-Ranges。

**分块传输编码 chunked**

可以把数据分割成多块，让浏览器逐步显示页面。

**多部分对象集合 boundary** 

一份报文主体内可含有多种类型的实体同时发送，每个部分之间用 boundary 字段定义的分隔符进行分隔，每个部分都可以有首部字段。

**虚拟主机**

HTTP/1.1 使用虚拟主机技术，使得一台服务器拥有多个域名，并且在逻辑上可以看成多个服务器。

#### 通信数据转发

**代理**

代理服务器接受客户端的请求，并且转发给其它服务器。

**目的**

- 缓存
- 负载均衡
- 网络访问控制
- 访问日志记录

**正向代理和反向代理**

用户察觉得到正向代理的存在。

![http2](/Users/wangjie/Desktop/面试知识点/pic/http2.png)

而反向代理一般位于内部网络中，用户察觉不到。

![http3](/Users/wangjie/Desktop/面试知识点/pic/http3.png)

**网关**

与代理服务器不同的是，网关服务器会将 HTTP 转化为其它协议进行通信，从而请求其它非 HTTP 服务器的服务。

**隧道**

使用 SSL 等加密手段，在客户端和服务器之间建立一条安全的通信线路。



## 5.Cookie&Session

#### Cookie

一次会话包含多次请求和响应，需要 Cookie 保存用户信息，在不登录的情况下完成服务端对客户端的身份识别，适合存储少量不太敏感的数据。

Cookie 是服务器发送到用户浏览器并保存在本地的一小块数据，它会在浏览器之后向同一服务器再次发起请求时被携带上，用于告知服务端两个请求是否来自同一浏览器。由于之后每次请求都会需要携带 Cookie 数据，会带来额外的性能开销（尤其是在移动环境下）。

HTTP/1.1 引入 Cookie 来保存状态信息，主要是为了让 HTTP 协议尽可能简单，使得它能够处理大量事务。HTTP 协议是无状态的，对于事务处理没有记忆能力，不对请求和响应之间的通信状态进行保存。

Cookie 曾一度用于客户端数据的存储，但渐渐被淘汰，新的浏览器 API 已经允许开发者直接将数据存储到本地，如使用 Web storage API（本地存储和会话存储）或 IndexedDB。

在 Tomcat 8 之前，Cookie 中不能直接存储中文数据，需要将中文数据转码，一般采用 URL 编码。在 Tomcat 8 之后，Cookie 支持中文数据。特殊字符还是不支持，建议使用 URL 编码存储，URL 解码解析。

**在服务端使用 Cookie**

1. 设置 Cookie 返回给客户端。创建多个 Cookie 对象，使用 response 调用多次 addCookie 方法，就可以发送多个 Cookie。

```java
@GetMapping("/change-username")
public String setCookie(HttpServletResponse response) {
    // 创建一个 cookie
    Cookie cookie = new Cookie("username", "Jovan");
    //设置 cookie过期时间
    cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
    //添加到 response 中
    response.addCookie(cookie);
		...
}

```

2. 使用 Spring 框架提供的 @CookieValue 注解获取特定的 Cookie 值

```java
@GetMapping("/")
public String readCookie(@CookieValue(value = "username", defaultValue = "Atta") String username) {
    return "Hey! My username is " + username;
}
```

3. 读取所有的 Cookie 值

```java
@GetMapping("/all-cookies")
public String readAllCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    ...
}
```

**用途**

- 会话状态管理（用户登录状态，购物车等）。

- 个性化设置。

- 浏览器行为跟踪，分析用户行为。

**创建过程**

1. 服务器发送的响应报文包含 Set-Cookie 首部字段，客户端得到响应报文后把 Cookie 内容保存到浏览器中。

2. 客户端之后对同一个服务器发送请求时，会从浏览器中取出 Cookie 信息并通过 Cookie 请求首部字段发送给服务器。

**分类**

- 会话期 Cookie：浏览器关闭之后它会被自动删除，也就是说它仅在会话期内有效。

- 持久性 Cookie：指定过期时间 Expires 或有效期 max-age 之后就成为了持久性的 Cookie。

#### Set-Cookie 属性

**Domain**

指定了哪些主机可以接受 Cookie。如果不指定，默认为当前文档的主机（不包含子域名）。如果指定了 Domain，则一般包含子域名。例如，如果设置 Domain=mozilla.org，则 Cookie 也包含在子域名中（如 developer.mozilla.org）。相同的一级域名，多个服务器可以共享 Cookie，如 tieba.baidu.com 和news.baidu.com。

**Path**

指定了主机下的哪些路径可以接受 Cookie（该 URL 路径必须存在于请求 URL 中）。以字符 / 作为路径分隔符，子路径也会被匹配。例如，设置 Path=/docs，则以下地址都会匹配：/docs，/docs/Web/，/docs/Web/HTTP。

setPath() 实现 Cookie 共享（默认情况下 Cookie 不能共享，只能在当前虚拟目录 application context 下共享）。

**HttpOnly**

标记为 HttpOnly 的 Cookie 不能被 JavaScript 脚本调用。

跨站脚本攻击 XSS 常常使用 JavaScript 的 document.cookie API 窃取用户的 Cookie 信息（创建和访问 Cookie），因此使用 HttpOnly 标记可以在一定程度上避免 XSS 攻击。

**Secure**

标记为 Secure 的 Cookie 只能通过被 HTTPS 协议加密过的请求发送给服务端。

但即便设置了 Secure 标记，敏感信息也不应该通过 Cookie 传输，因为 Cookie 有其固有的不安全性，Secure 标记也无法提供确实的安全保障。

**Expires/Max-Age**

持久化存储 Cookie。

默认浏览器关闭后，Cookie 数据被销毁。

#### **Token**

一般的网站都会有保持登录，也就是说下次你再访问网站的时候，不需要重新登录就能完成服务器对客户端的身份识别。这是因为用户登录的时候我们可以存放了一个令牌 Token 在 Cookie 中，下次登录的时候只需要根据 Token 值来查找用户即可。为了安全考虑，重新登录一般要将 Token 重写。

JWT 本质上就一段签名的 JSON 格式的数据。由于它是带有签名的，因此接收者便可以验证它的真实性。

在基于 Token 进行身份验证的的应用程序中，服务器通过 Payload、Header 和一个密钥 secret 创建 Token，并将 Token 发送给客户端，客户端将 Token 保存在 Cookie 或者 localStorage 里面，以后客户端发出的所有请求都会携带这个令牌。

你可以把它放在 Cookie 里面自动发送，但是这样不能跨域，所以更好的做法是放在 HTTP Header 的 Authorization 字段中，Authorization: Bearer Token。

**无状态**

Token 自身包含了身份验证所需要的所有信息，使得我们的服务器不需要存储 Session 信息，这显然增加了系统的可用性和伸缩性，大大减轻了服务端的压力。更加适合移动端。

缺点：当后端在 Token 有效期内废弃一个 Token 或者更改它的权限的话，不会立即生效，一般需要等到有效期过后才可以。另外，当用户 Logout 的话，Token 也还有效。除非，我们在后端增加额外的处理逻辑。

#### Session 

将用户信息存储在服务器端，存储在服务器端的信息更加安全。

Session 可以存储在服务器上的文件、数据库或者内存中。也可以将 Session 存储在 Redis 这种内存型数据库中，效率会更高。

**使用 Session 维护用户登录状态的过程**

1. 用户进行登录时，用户提交包含用户名和密码的表单，放入 HTTP 请求报文中；

2. 服务器验证该用户名和密码，如果正确则把用户信息存储到 Redis 中，它在 Redis 中的 Key 称为 Session ID；

3. 服务器返回的响应报文的 Set-Cookie 首部字段包含了这个 Session ID，客户端收到响应报文之后将该 Cookie 值存入浏览器中；

4. 客户端之后对同一个服务器进行请求时会包含该 Cookie 值，服务器收到之后提取出 Session ID，从 Redis 中取出用户信息，继续之前的业务操作。

**注意**

- 应该注意 Session ID 的安全性问题，不能让它被恶意攻击者轻易获取，那么就不能产生一个容易被猜到的 Session ID 值。

- 此外，还需要经常重新生成 Session ID。在对安全性要求极高的场景下（如转账），除了使用 Session 管理用户状态之外，还需要对用户进行重新验证（如重新输入密码，或者使用短信验证码等）。

- 确保客户端开启了 Cookie。
- 注意 Session 的过期时间。

**应用**

- 典型的场景是购物车：要添加商品到购物车的时候，系统不知道是哪个用户操作的，服务端给特定的用户创建特定的 Session 之后就可以标识这个用户并且跟踪这个用户了。一般情况下，服务器会在一定时间内保存这个 Session，过了时间限制，就会销毁这个 Session。

- 可以通过 Session 来统计网站的用户在线数。



客户端关闭后，服务器不关闭，两次获取的 Session 默认不是同一个。如果需要相同，创建 Cookie 键为 JSESSIONID，设置最大存活时间，让 Cookie 持久化保存。

客户端不关闭，服务器关闭后，两次获取的 Session 不是同一个。需要在服务器关闭前将 Session 对象持久化到硬盘，并在服务器启动后转化为内存中的 Session 对象。Tomcat 会自动完成这一步，IDEA 可以钝化但不可以活化（因为重新创建了 work 目录）。

Session 的销毁：服务器关闭。Session 对象调用 invalidate()。Session 默认失效时间为 30 分钟（可以修改配置）。

Spring Session 提供了一种跨多个应用程序或实例管理用户会话信息的机制。

隐藏的表单域：< input type="hidden" name ="session" value="..."/>，所有页面必须是表单提交之后的结果。

#### **集群下的 Session 管理**

一个用户的 Session 信息如果存储在一个服务器上，那么当负载均衡器把用户的下一个请求转发到另一个服务器，由于服务器没有用户的 Session 信息，那么该用户就需要重新进行登录等操作。

**Sticky Session**

需要配置负载均衡器，使用 IP Hash 使得一个用户的所有请求都路由到同一个服务器，这样就可以把用户的 Session 存放在该服务器中。

**缺点**

当服务器宕机时，将丢失该服务器上的所有 Session。

**Session Replication**

在服务器之间进行 Session 同步操作，每个服务器都有所有用户的 Session 信息，因此用户可以向任何一个服务器进行请求。

**缺点**

占用过多内存。

同步过程占用网络带宽以及服务器处理器时间。

**Session Server**

使用一个单独的服务器存储 Session 数据，可以使用传统的 MySQL，也使用 Redis 或者 Memcached 这种内存型数据库。

**优点**

为了使得大型网站具有伸缩性，集群中的应用服务器通常需要保持无状态，那么应用服务器不能存储用户的会话信息。Session Server 将用户的会话信息单独进行存储，从而保证了应用服务器的无状态。

**缺点**

需要去实现存取 Session 的代码。

如果 Session 集中服务器有问题，会影响应用。

**浏览器禁用 Cookie**

使用 Session 和 URL 重写。

此时无法使用 Cookie 来保存用户信息，只能使用 Session。除此之外，不能再将 Session ID 存放到 Cookie 中，而是使用 URL 重写技术，把 Session ID 直接附加在 URL 路径的后面，将 Session ID 作为 URL 的参数进行传递。服务器能够解析重写后的 URL 获取 Session ID。但是安全性和用户体验感降低。可以对 Session ID 进行一次加密之后再传入后端。

#### Cookie 与 Session 比较

Cookie 和 Session 都是用来跟踪浏览器用户身份的会话方式。

- Cookie 只能存储 ASCII 码字符串，而 Session 则可以存储任何类型的数据，因此在考虑数据复杂性时首选 Session。

- Cookie 存储在浏览器中，容易被恶意查看和伪造。如果非要将一些隐私数据存在 Cookie 中，可以将 Cookie 值进行加密，然后在服务器进行解密。而 Session 存储在服务器中，安全性高，但对于大型网站，如果用户所有的信息都存储在 Session 中，那么开销是非常大的，因此不建议将所有的用户信息都存储到 Session 中。

- Cookie 是需要浏览器支持的，可能被禁用。而 Session 不会。

- Cookie 支持跨域名访问，Session 仅在所在的域名内有效。



## 6.HTTPS

**HTTP 的安全性问题**

- 使用明文进行通信，内容可能会被窃听。
- 不验证通信方的身份，通信方的身份有可能遭遇伪装。
- 无法证明报文的完整性，报文有可能遭篡改。

**HTTPS**

HTTPS 让 HTTP 先和 SSL 通信，再由 SSL 和 TCP 通信，也就是说 HTTPS 使用了隧道进行通信。

通过使用 SSL，HTTPS 具有了加密（防窃听）、认证（防伪装）和完整性保护（防篡改）。

#### 加密

HTTPS 采用混合的加密机制，使用非对称密钥加密用于传输对称密钥，来保证传输过程的安全性。之后使用对称密钥加密进行通信来保证通信过程的效率。

**对称密钥加密**

加密和解密使用同一密钥。

优点：运算速度快。

缺点：无法安全地将密钥传输给通信方。

**非对称密钥加密**

发送方获得接收方的公开密钥之后，就可以使用公开密钥进行加密，接收方收到通信内容后使用私有密钥解密。

非对称密钥除了用来加密，还可以用来进行签名。因为私有密钥无法被其他人获取，因此通信发送方使用其私有密钥进行签名，通信接收方使用发送方的公开密钥对签名进行解密，就能判断这个签名是否正确。

DH 密钥交换算法，RSA 算法最广为使用。

#### 认证

通过使用证书来对通信方进行认证。

数字证书认证机构 CA 是客户端与服务器双方都可信赖的第三方机构。

1. 服务器的运营人员向 CA 提出公开密钥的申请，CA 在判明提出申请者的身份之后，会对已申请的公开密钥做数字签名，然后分配这个已签名的公开密钥，并将该公开密钥放入公开密钥证书后绑定在一起。

2. 进行 HTTPS 通信时，服务器会把证书发送给客户端。客户端取得其中的公开密钥之后，先使用数字签名进行验证，如果验证通过，就可以开始通信了。

#### 完整性保护

SSL 提供报文摘要功能来进行完整性保护。

HTTP 也提供了 MD5 报文摘要功能，但不是安全的。例如报文内容被篡改之后，同时重新计算 MD5 的值，通信接收方是无法意识到发生了篡改。

HTTPS 的报文摘要功能之所以安全，是因为它结合了加密和认证这两个操作。试想一下，加密之后的报文，遭到篡改之后，也很难重新计算报文摘要，因为无法轻易获取明文。

**HTTPS 的缺点**

- 因为需要进行加密解密等过程，耗费资源，因此速度会更慢。

- 需要支付证书授权的高额费用。

#### HTTP 和 HTTPS 的区别

- HTTP 传输是未加密的明文，而 HTTPS 经过 SSL 加密、认证和完整性保护更加安全。但是 HTTPS 比 HTTP 耗费更多服务器资源。

- HTTPS 协议需要到 CA 申请证书。

- HTTP 使用简单的无状态的连接，HTTPS 通过 SSL 连接。

- HTTP 使用 80 端口，HTTPS 使用 443 端口。

#### SSL/TLS 安全协议

确保互联网连接安全。TLS 是更为安全的升级版 SSL。SSL/TLS 是有状态的。

**SSL/TLS 握手过程**

1. Client Hello：生成 Random1

2. Server Hello：生成 Random2，这两个随机数会在后续生成对称秘钥时用到。

3. Certificate：这一步是服务端将自己的证书下发给客户端，让客户端验证自己的身份，客户端验证通过后取出证书中的公钥。

4. Server Key Exchange：如果是 DH 算法，这里发送服务器使用的 DH 参数。RSA 算法不需要这一步。

5. Certificate Request：服务端要求客户端上报证书，这一步是可选的，对于安全性要求高的场景会用到。

6. Server Hello Done

7. Certificate Verify：客户端收到服务端传来的证书后，先从 CA 验证该证书的合法性，验证通过后取出证书中的服务端公钥，再生成 Random3，再用服务端公钥非对称加密 Random3 生成 PreMaster Key。

8. Client Key Exchange：客户端将 PreMaster Key 传给服务端，服务端再用自己的私钥解出这个 PreMaster Key 得到客户端生成的 Random3。至此，客户端和服务端都拥有 Random1 + Random2 + Random3，两边再根据同样的算法就可以生成一份秘钥，握手结束后的应用层数据都是使用这个秘钥进行对称加密。

**注意**

握手阶段和其后的会话，服务器的公钥和私钥只需要用到一次。

服务器公钥放在服务器的数字证书之中。

采用 DH 算法后，PreMaster Key 不需要传递，双方只要交换各自的参数，就可以算出这个随机数。

**Session 的恢复**

握手阶段用来建立 SSL 连接。如果出于某种原因，对话中断，就需要重新握手。这时有两种方法可以恢复原来的 Session：一种叫做 Session ID，另一种叫做 Session ticket。

**Session ID**

每一次对话都有一个编号 Session ID。如果对话中断，下次重连的时候，只要客户端给出这个编号，且服务器有这个编号的记录，双方就可以重新使用已有的对话密钥，而不必重新生成一把。

**缺点**

Session ID 往往只保留在一台服务器上。所以，如果客户端的请求发到另一台服务器，就无法恢复对话。

**Session ticket**

发送一个服务器在上一次对话中发送过来的 Session ticket。这个 Session ticket 是加密的，只有服务器才能解密，其中包括本次对话的主要信息，比如对话密钥和加密方法。当服务器收到 Session ticket 以后，解密后就不必重新生成对话密钥了。



## 7.HTTP 版本变迁

#### HTTP/1.1 对比 1.0 的新特性

- 默认是长连接

- 支持流水线

- 支持同时打开多个 TCP 连接

- 支持虚拟主机

- 新增状态码 100

- 支持分块传输编码

- 新增缓存处理指令 max-age

#### HTTP/1.x 的缺陷

实现简单是以牺牲性能为代价的：

- 客户端需要使用多个连接才能实现并发和缩短延迟。
- 不会压缩请求和响应首部，从而导致不必要的网络流量。
- 不支持有效的资源优先级，致使底层 TCP 连接的利用率低下。
- 队头堵塞 Head-of-line blocking：同一个 TCP 连接里面，所有的数据通信是按次序进行的。服务器只有处理完一个回应，才会进行下一个回应。要是前面的回应特别慢，后面就会有许多请求排队等着。为了避免这个问题，只有两种方法：一是减少请求数，二是同时多开持久连接。这导致了很多的网页优化技巧，比如合并脚本和样式表、将图片嵌入 CSS 代码、域名分片 domain sharding 等等。

#### HTTP/2 采用的技术

谷歌自行研发的基于 TCP 的 SPDY 协议作为 HTTP/2 的基础。

**二进制分帧层**

将报文分成 HEADERS 帧和 DATA 帧，它们都是二进制格式的。

在通信过程中，只会有一个 TCP 连接存在，它承载了任意数量的双向数据流 Stream。Stream 对数据包做标记，指出它属于哪个回应。客户端还可以指定数据流的优先级。优先级越高，服务器就会越早回应。

**服务端推送**

在客户端请求一个资源时，会把相关的资源一起发送给客户端，客户端就不需要再次发起请求了。

**首部压缩**

HTTP/2.0 要求客户端和服务器同时维护和更新一个包含之前见过的首部字段表，从而避免了重复传输。

HTTP/1.1 的首部带有大量信息，而且每次都要重复发送。

**多工 multiplexing**

在一个连接里，客户端和浏览器都可以同时发送多个请求或回应，而且不用按照顺序一一对应，这样就避免了队头堵塞。首先考虑 HTTP/2 使用 multiplexing，不行则在一个 host 上建立多个 TCP 连接。

**有状态组件**



## 8.同源和跨域

#### 同源策略

协议/主机/端口都相同。

是一个重要的浏览器安全策略，限制了一个 origin 的文档或者它加载的脚本与另一个源的资源进行交互，它能够帮助阻隔恶意文档，减少可能被攻击的媒介。

#### 跨域资源共享 CORS

可以解决 AJAX 跨域请求问题。

CORS 有两种请求，简单请求和复杂请求。复杂跨域请求会触发预检机制。

对于简单跨域请求，后端只需要在响应体里返回 Access-Controll-Allow 就可以了，但是对于复杂请求，则需要域名白名单 Access-Control-Allow-Origin:http://xxx，或者将请求调整为一个简单请求。

请求首部字段 Origin 指示了请求来自于哪个站点，不会对 cookie 认证造成影响，伪造 Origin 与 CORS 并没有关系，可能会由于后台的漏洞将 AJAX 作为一种攻击手段。

**复杂请求的预检流程**

1. 前端 AJAX 请求前发出一个 OPTIONS 预检，会带一堆相关头部发送给服务端。

2. 服务端在接受到预检时，检查头部，来源等信息是否合法，合法则接下来允许正常的请求，否则拒绝。

3. 浏览器端如果收到服务端拒绝的信息（响应头部检查），就抛出对应错误。

4. 否则就是正常的响应，接下来发出真正的请求（如 POST）。

#### AJAX

通过在后台与服务器进行少量数据交换，AJAX 可以使网页实现异步更新。这意味着可以在不重新加载整个网页的情况下，对网页的某部分进行更新。提升用户的体验。

传统的网页如果需要更新内容，必须重载整个网页页面。

AJAX 请求是否安全，由服务端决定。AJAX 出现后，请求方式变多了，以前的架构在新的请求中就可能出现更多漏洞。

AJAX 受到浏览器的同源策略限制。

AJAX 默认无法请求跨域的接口，当然后台可以配置 Access-Control-Allow-Origin:* 之类的允许所有的跨域请求。

AJAX 请求无法携带跨域 cookie，如果强行开启 withCredentials，必须服务端配合认证，无法用作攻击。

从本质上讲：AJAX 就是浏览器发出的 HTTP 请求，只不过是浏览器加上了一个同源策略限制而已。AJAX 请求的 XMLHTTPRequest 对象就是浏览器开放给 JS 调用 HTTP 请求用的。

**AJAX 和 HTTP 的区别**

AJAX 请求受到浏览器的同源策略限制，存在跨域问题。

AJAX 在进行复杂请求时，浏览器会预先发出 OPTIONS 预检，而 HTTP 是不会预检的。

从使用角度上说，AJAX 使用简单一点，少了些底层细节，多了些浏览器特性（如自动带上同域 cookie 等）。

和认证上的 HTTP 请求的区别就是多了一次浏览器的封装而已（浏览器会有自己的预处理，加上特定限制）。

但是，从最终发出的报文来看，内容都是一样的（HTTP 协议规范的内容），AJAX 是发送 HTTP 请求的一种方式。

AJAX 本质上安全性和 HTTP 请求一样。

#### 跨站请求伪造 CSRF

使用 Cookie 进行用户校验，在不登出受信任网站 A 的情况下，访问危险网站 B，冒用用户身份，进行恶意操作。

CSRF 利用的是网站对用户浏览器的信任。

CSRF 与 AJAX 无关，AJAX 受到浏览器的同源策略限制。

**防御手段**

**检查 Referer 首部字段**

Referer 首部字段位于 HTTP 报文中，用于标识请求来源的地址。检查这个首部字段并要求请求来源的地址在同一个域名下，可以极大的防止 CSRF 攻击。

这种办法简单易行，工作量低，仅需要在关键访问处增加一步校验。

但这种办法也有其局限性，因其完全依赖浏览器发送正确的 Referer 字段。虽然 HTTP 协议对此字段的内容有明确的规定，但并无法保证来访的浏览器的具体实现，亦无法保证浏览器没有安全漏洞影响到此字段。并且也存在攻击者攻击某些浏览器，篡改其 Referer 字段的可能。

**添加校验 Token**

在访问敏感数据请求时，要求用户浏览器提供不保存在 Cookie 中，并且用攻击者无法伪造的数据作为校验。

例如服务器生成随机数并附加在页面表单中作为 Token，并要求客户端传回这个随机数。伪造的请求无法获得该值，CSRF 伪造用户请求，需要构造用户请求的所有参数才可以，阻止攻击者获得所有请求参数。

**输入验证码**

因为 CSRF 攻击是在用户无意识的情况下发生的，所以要求用户输入验证码可以让用户知道自己正在做的操作。

或者进入危险网站时浏览器发出警告。

#### 跨站脚本攻击 XSS

可以将代码注入到用户浏览的网页上，这种代码包括 HTML 和 JavaScript。

不论是 Cookie 还是 Token 都无法避免 XSS。

**危害**

- 窃取用户的 Cookie。
- 伪造虚假的输入表单骗取个人信息。
- 显示伪造的文章或者图片。

**防御手段**

- 设置 Cookie 为 HttpOnly：可以防止 JavaScript 脚本调用，就无法通过 document.cookie 获取用户 Cookie 信息。

- 过滤特殊字符：例如将 < 转义为 &lt，将 > 转义为 &gt，从而避免 HTML 和 Jascript 代码的运行。

  富文本编辑器允许用户输入 HTML 代码，就不能简单地将 < 等字符进行过滤了，极大地提高了 XSS 攻击的可能性。富文本编辑器通常采用 XSS filter 来防范 XSS 攻击，通过定义一些标签白名单或者黑名单，从而不允许有攻击性的 HTML 代码的输入。



## 9.其他

#### **URL 和 URI**

统一资源定位符 URL，统一资源标志符 URI。

URL 是一种具体的 URI，它不仅唯一标识资源，而且还提供了定位该资源的信息。

#### REST

一种万维网软件架构风格，目的是便于不同软件/程序在网络中互相传递信息。表现层状态转换是根基于 HTTP 之上而确定的一组约束和属性，是一种设计提供万维网络服务的软件构建风格。

允许客户端发出以统一资源标识符访问和操作网络资源的请求，而与预先定义好的无状态操作集一致化。因此表现层状态转换提供了在互联网络的计算系统之间，彼此资源可交互使用的协作性质。

当前在三种主流的 web 服务实现方案中，REST 模式与复杂的 SOAP 和 XML-RPC 相比更加简洁。

互联网通信协议 HTTP 协议，是一个无状态协议。这意味着，所有的状态都保存在服务器端。因此，如果客户端想要操作服务器，必须通过某种手段，让服务器端发生状态转化。而这种转化是建立在表现层之上的，所以就是表现层状态转化。

RESTful 利于缓存，请求的路径一样但请求方法不一样也可以。或者 /user 和 /user/{id} 然后请求方法一样，也可以。

RESTful 框架天然支持跨语言。

**资源**

网络上的一个实体，或者说是网络上的一个具体信息。可以用一个统一资源标识符 URI 指向它。

**表现层**

把资源具体呈现出来的形式，表现为各种格式，XML 或者 HTML。客户端和服务器之间，传递资源的某种表现形式。

**状态转化**

访问一个网站，就代表了客户端和服务器的一个互动过程。在这个过程中，势必涉及到数据和状态的变化。

#### JSON

JS 对象表示法，多用于存储和交换文本信息的语法，进行数据的传输，比 XML 更小、更快、更易解析。

Jackson 是 SpringMVC 内置的 JSON 解析器。


