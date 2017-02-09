/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pj.client.core;

/**
 * 这种错误是抛出发到客户端
 * 可以显示给用户看的
 * @author PENGJU
 * email:pengju114@163.com
 * 时间:2012-9-20 10:48:28
 */
public class ClientException extends Exception{
    public static final int REQUEST_ERROR=-10;
    public static final int REQUEST_OK=0;
    public static final int REQUEST_NOT_LOGIN=-1;
    public static final int REQUEST_INVALID_PARAMETER=-2;
    public static final int REQUEST_ILLEGAL_ACCESS=-4;
    
    /**
    *  请求错误（4字头）编辑,即将正常的Response错误码加上'-'负号
    * 1、语义有误，当前请求无法被服务器理解。除非进行修改，否则客户端不应该重复提交这个请求。
    * 2、请求参数有误。
    */
    public static final int BAD_REQUEST  = -400;
    /**
    *  当前请求需要用户验证。该响应必须包含一个适用于被请求资源的 WWW-Authenticate 信息头用以询问用户信息。
    *  客户端可以重复提交一个包含恰当的 Authorization 头信息的请求。
    *  如果当前请求已经包含了 Authorization 证书，那么401响应代表着服务器验证已经拒绝了那些证书。
    *  如果401响应包含了与前一个响应相同的身份验证询问，且浏览器已经至少尝试了一次验证，那么浏览器应当向用户展示响应中包含的实体信息，因为这个实体信息中可能包含了相关诊断信息。参见RFC 2617。
    */
    public static final int UNAUTHORIZED = -401;
    /**  该状态码是为了将来可能的需求而预留的。*/
    public static final int PAYMENT_REQUIRED = -402;
    /**
    *  服务器已经理解请求，但是拒绝执行它。
    *  与401响应不同的是，身份验证并不能提供任何帮助，而且这个请求也不应该被重复提交。
    *  如果这不是一个 HEAD 请求，而且服务器希望能够讲清楚为何请求不能被执行，那么就应该在实体内描述拒绝的原因。
    *  当然服务器也可以返回一个404响应，假如它不希望让客户端获得任何信息。
    */
    public static final int FORBIDDEN = -403;
    /**
    *  请求失败，请求所希望得到的资源未被在服务器上发现。
    *  没有信息能够告诉用户这个状况到底是暂时的还是永久的。
    *  假如服务器知道情况的话，应当使用410状态码来告知旧资源因为某些内部的配置机制问题，已经永久的不可用，而且没有任何可以跳转的地址。
    *  404这个状态码被广泛应用于当服务器不想揭示到底为何请求被拒绝或者没有其他适合的响应可用的情况下。
    *  出现这个错误的最有可能的原因是服务器端没有这个页面。
    */
    public static final int NOT_FOUND = -404;
/**
    *  请求行中指定的请求方法不能被用于请求相应的资源。
    *  该响应必须返回一个Allow 头信息用以表示出当前资源能够接受的请求方法的列表。
    *  鉴于 PUT，DELETE 方法会对服务器上的资源进行写操作，
    *  因而绝大部分的网页服务器都不支持或者在默认配置下不允许上述请求方法，对于此类请求均会返回405错误。
    */
    public static final int METHOD_NOT_ALLOWED = -405;
    /**
    *  请求的资源的内容特性无法满足请求头中的条件，因而无法生成响应实体。
    *  除非这是一个 HEAD 请求，
    *  否则该响应就应当返回一个包含可以让用户或者浏览器从中选择最合适的实体特性以及地址列表的实体。
    *  实体的格式由 Content-Type 头中定义的媒体类型决定。
    *  浏览器可以根据格式及自身能力自行作出最佳选择。
    *  但是，规范中并没有定义任何作出此类自动选择的标准。
    */
    public static final int NOT_ACCEPTABLE = -406;
    /**
    *  与401响应类似，只不过客户端必须在代理服务器上进行身份验证。
    *  代理服务器必须返回一个 Proxy-Authenticate 用以进行身份询问。
    *  客户端可以返回一个 Proxy-Authorization 信息头用以验证。参见RFC 2617。
    */
    public static final int PROXY_AUTHENTICATION_REQUIRED = -407;
    /**
    *  请求超时。客户端没有在服务器预备等待的时间内完成一个请求的发送。
    *  客户端可以随时再次提交这一请求而无需进行任何更改。
    */
    public static final int REQUEST_TIMEOUT = -408;
    /**
    *  由于和被请求的资源的当前状态之间存在冲突，请求无法完成。
    *  这个代码只允许用在这样的情况下才能被使用：用户被认为能够解决冲突，并且会重新提交新的请求。
    *  该响应应当包含足够的信息以便用户发现冲突的源头。
    *  冲突通常发生于对 PUT 请求的处理中。
    *  例如，在采用版本检查的环境下，
    *  某次 PUT 提交的对特定资源的修改请求所附带的版本信息与之前的某个（第三方）请求向冲突，
    *  那么此时服务器就应该返回一个409错误，告知用户请求无法完成。
    *  此时，响应实体中很可能会包含两个冲突版本之间的差异比较，以便用户重新提交归并以后的新版本。
    */
    public static final int CONFLICT = -409;

    /**
     * 被请求的资源在服务器上已经不再可用，而且没有任何已知的转发地址。
     * 这样的状况应当被认为是永久性的。
     * 如果可能，拥有链接编辑功能的客户端应当在获得用户许可后删除所有指向这个地址的引用。
     * 如果服务器不知道或者无法确定这个状况是否是永久的，那么就应该使用404状态码。
     * 除非额外说明，否则这个响应是可缓存的。
     */
    public static final int GONE = -410;

    /**
     * 服务器拒绝在没有定义 Content-Length 头的情况下接受请求。
     * 在添加了表明请求消息体长度的有效 Content-Length 头之后，客户端可以再次提交该请求。
     */
    public static final int LENGTH_REQUIRED = -411;

    /**
     * 服务器在验证在请求的头字段中给出先决条件时，没能满足其中的一个或多个。
     * 这个状态码允许客户端在获取资源时在请求的元信息（请求头字段数据）中设置先决条件，
     * 以此避免该请求方法被应用到其希望的内容以外的资源上。
     */
    public static final int PRECONDITION_FAILED = -412;

//413 Request Entity Too Large
//服务器拒绝处理当前请求，因为该请求提交的实体数据大小超过了服务器愿意或者能够处理的范围。此种情况下，服务器可以关闭连接以免客户端继续发送此请求。
//如果这个状况是临时的，服务器应当返回一个 Retry-After 的响应头，以告知客户端可以在多少时间以后重新尝试。
//414 Request-URI Too Long
//请求的URI 长度超过了服务器能够解释的长度，因此服务器拒绝对该请求提供服务。这比较少见，通常的情况包括：
//本应使用POST方法的表单提交变成了GET方法，导致查询字符串（Query String）过长。
//重定向URI “黑洞”，例如每次重定向把旧的 URI 作为新的 URI 的一部分，导致在若干次重定向后 URI 超长。
//客户端正在尝试利用某些服务器中存在的安全漏洞攻击服务器。这类服务器使用固定长度的缓冲读取或操作请求的 URI，当 GET 后的参数超过某个数值后，可能会产生缓冲区溢出，导致任意代码被执行[1]。没有此类漏洞的服务器，应当返回414状态码。
//415 Unsupported Media Type
//对于当前请求的方法和所请求的资源，请求中提交的实体并不是服务器中所支持的格式，因此请求被拒绝。
//416 Requested Range Not Satisfiable
//如果请求中包含了 Range 请求头，并且 Range 中指定的任何数据范围都与当前资源的可用范围不重合，同时请求中又没有定义 If-Range 请求头，那么服务器就应当返回416状态码。
//假如 Range 使用的是字节范围，那么这种情况就是指请求指定的所有数据范围的首字节位置都超过了当前资源的长度。服务器也应当在返回416状态码的同时，包含一个 Content-Range 实体头，用以指明当前资源的长度。这个响应也被禁止使用 multipart/byteranges 作为其 Content-Type。
//417 Expectation Failed
//在请求头 Expect 中指定的预期内容无法被服务器满足，或者这个服务器是一个代理服务器，它有明显的证据证明在当前路由的下一个节点上，Expect 的内容无法被满足。
//421There are too many connections from your internet address
//从当前客户端所在的IP地址到服务器的连接数超过了服务器许可的最大范围。通常，这里的IP地址指的是从服务器上看到的客户端地址（比如用户的网关或者代理服务器地址）。在这种情况下，连接数的计算可能涉及到不止一个终端用户。
//422 Unprocessable Entity
//请求格式正确，但是由于含有语义错误，无法响应。（RFC 4918 WebDAV）
//423 Locked
//当前资源被锁定。（RFC 4918 WebDAV）
//424 Failed Dependency
//由于之前的某个请求发生的错误，导致当前请求失败，例如 PROPPATCH。（RFC 4918 WebDAV）
//425 Unordered Collection
//在WebDav Advanced Collections 草案中定义，但是未出现在《WebDAV 顺序集协议》（RFC 3658）中。
//426 Upgrade Required
//客户端应当切换到TLS/1.0。（RFC 2817）
//449 Retry With
//由微软扩展，代表请求应当在执行完适当的操作后进行重试。
//服务器错误（5、6字头）编辑
//这类状态码代表了服务器在处理请求的过程中有错误或者异常状态发生，也有可能是服务器意识到以当前的软硬件资源无法完成对请求的处理。除非这是一个HEAD 请求，否则服务器应当包含一个解释当前错误状态以及这个状况是临时的还是永久的解释信息实体。浏览器应当向用户展示任何在当前响应中被包含的实体。
//这些状态码适用于任何响应方法。
//500 Internal Server Error
//服务器遇到了一个未曾预料的状况，导致了它无法完成对请求的处理。一般来说，这个问题都会在服务器端的源代码出现错误时出现。
//501 Not Implemented
//服务器不支持当前请求所需要的某个功能。当服务器无法识别请求的方法，并且无法支持其对任何资源的请求。
//502 Bad Gateway
//作为网关或者代理工作的服务器尝试执行请求时，从上游服务器接收到无效的响应。
//503 Service Unavailable
//由于临时的服务器维护或者过载，服务器当前无法处理请求。这个状况是临时的，并且将在一段时间以后恢复。如果能够预计延迟时间，那么响应中可以包含一个 Retry-After 头用以标明这个延迟时间。如果没有给出这个 Retry-After 信息，那么客户端应当以处理500响应的方式处理它。
//注意：503状态码的存在并不意味着服务器在过载的时候必须使用它。某些服务器只不过是希望拒绝客户端的连接。
//504 Gateway Timeout
//作为网关或者代理工作的服务器尝试执行请求时，未能及时从上游服务器（URI标识出的服务器，例如HTTP、FTP、LDAP）或者辅助服务器（例如DNS）收到响应。
//注意：某些代理服务器在DNS查询超时时会返回400或者500错误
//505 HTTP Version Not Supported
//服务器不支持，或者拒绝支持在请求中使用的 HTTP 版本。这暗示着服务器不能或不愿使用与客户端相同的版本。响应中应当包含一个描述了为何版本不被支持以及服务器支持哪些协议的实体。
//506 Variant Also Negotiates
//由《透明内容协商协议》（RFC 2295）扩展，代表服务器存在内部配置错误：被请求的协商变元资源被配置为在透明内容协商中使用自己，因此在一个协商处理中不是一个合适的重点。
//507 Insufficient Storage
//服务器无法存储完成请求所必须的内容。这个状况被认为是临时的。WebDAV (RFC 4918)
//509 Bandwidth Limit Exceeded
//服务器达到带宽限制。这不是一个官方的状态码，但是仍被广泛使用。
//510 Not Extended
//获取资源所需要的策略并没有没满足。（RFC 2774）
//600 Unparseable Response Headers
//源站没有返回响应头部，只返回实体内容
    
    
    private final int errorCode;

    public ClientException(int errorCode) {
        super();
        this.errorCode=errorCode;
    }

    public ClientException(int errorCode,String message) {
        super(message);
        this.errorCode=errorCode;
    }

    public ClientException(int errorCode,String message, Throwable cause) {
        super(message, cause);
        this.errorCode=errorCode;
    }


    public ClientException(int errorCode,Throwable cause) {
        super(cause);
        this.errorCode=errorCode;
    }
    
    public int getErrorCode(){
        return errorCode;
    }
}
