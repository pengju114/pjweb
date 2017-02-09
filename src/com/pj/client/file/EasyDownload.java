package com.pj.client.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

/**
 *
 * @author pengju
 */
public class EasyDownload {
    private static final int BUFFER_SIZE=1024*3;//3KB缓冲区
    private HttpServletResponse response=null;
    private PageContext pageContext=null;
    private String charset="UTF-8";//默认编码格式
    private String filePath="";//要下载的文件路径(绝对路径)

    /**
     * JSP下载方式构造函数
     * @param pageContext jsp页面上下文
     */
    public EasyDownload(PageContext pageContext){
        this.pageContext=pageContext;
        this.response=(HttpServletResponse) pageContext.getResponse();
    }

    /**
     * Servlet下载方式构造函数
     * @param response
     */
    public EasyDownload(HttpServletResponse response){
        this.response=response;
    }

    public String getCharset(){
        return charset;
    }
    public void setCharset(String charset){
        this.charset=charset;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void download()throws IOException{
        response.reset();
        response.setContentType("application/octet-stream");

        File file=new File(filePath);
        if(!file.exists()){
            response.sendError(response.SC_NOT_FOUND,"没有找到文件[File Not Found]");
            return;
        }
        response.setContentLength((int)file.length());//如果没有这一句用迅雷下载就会有问题

        String fileName=URLEncoder.encode(file.getName(), charset);
        response.addHeader("Content-Disposition","attachment;filename="+fileName);

        FileInputStream in=new FileInputStream(file);
        BufferedOutputStream out=new BufferedOutputStream(response.getOutputStream());
        byte[] buf=new byte[BUFFER_SIZE];
        int readed=0;
        while((readed=in.read(buf))!=-1){
            out.write(buf,0,readed);
        }
        out.flush();
        in.close();

        if(pageContext!=null){//JSP 下载方式
            JspWriter w=pageContext.getOut();
            w.clear();
            w=pageContext.pushBody();
        }else{//Servlet方式
            out.close();
        }
    }

}
