
package com.pj.client.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author pengju 基于《精通Servlet》一书的例子
 */
public class EasyUpload {

    private HttpServletRequest request = null;
    private ServletInputStream in = null;
    private MultipartStreamReader msr = null;
    private String saveDir = null;
    private String boundary = null;
    private String fileSeparator = null;
    private Param items = null;
    private LinkedList files = null;
    private String charset = null;
    private boolean useRandomName = false;//是否使用随机文件名
    private boolean useUploadStatus = false;//是否使用上传状态，使用的话将保存在session里面
    private static final int BUFFER_SIZE = 1024 * 3;//3KB
    private long totalContent;
    private long contentLength;
    private UploadListener listener;
    /**
     * 可通过 (UploadStatus)session.getAttrbute(EasyUpload.UPLOAD_STATUS);
     * 取上传状态信息，返回UploadStatus接口实例
     */
    public static final String UPLOAD_STATUS = "MiniUploadStatus";

    public EasyUpload(HttpServletRequest request, String saveDir) {
        this(request, saveDir, "ISO-8859-1");
    }

    public EasyUpload(HttpServletRequest request, String saveDir, String charset) {
        this.request = request;
        this.saveDir = saveDir;
        try {
            in = this.request.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        msr = new MultipartStreamReader();
        items = new Param();
        fileSeparator = System.getProperty("file.separator");
        files = new LinkedList();
        this.charset = charset;
        this.totalContent = 0L;
        this.contentLength = request.getContentLength();
        this.listener = null;
    }

    public Parameter getFormItems() {
        return this.items;
    }

    public LinkedList getFiles() {
        return files;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setUseRandomName(boolean useRandomName) {
        this.useRandomName = useRandomName;
    }

    public boolean isUseRandomName() {
        return this.useRandomName;
    }

    public void setUseUploadStatus(boolean useUploadStatus) {
        this.useUploadStatus = useUploadStatus;
    }

    public boolean isUseUploadStatus() {
        return this.useUploadStatus;
    }

    public void setUploadListener(UploadListener listener) {
        this.listener = listener;
    }

    private String getFileName(String name) {
        if (!this.useRandomName) {
            return name;
        }
        int index = name.lastIndexOf(".");
        String ext = ".unknown";
        if (index >= 0) {
            ext = name.substring(index);
        }

        String name1 = String.valueOf(System.currentTimeMillis());
        name1 = name1.substring(name1.length() / 3);
        String name2 = String.valueOf(Math.random());
        name2 = name2.substring(name2.length() / 3);
        return (name1 + name2 + ext);
    }

    public void upload() throws Exception {
        String contentType = this.request.getContentType();
//        if (!contentType.toLowerCase().startsWith("multipart/form-data")) {
//            throw new IOException("请求类型不是multipart/form-data类型");
//        }

        this.boundary = getBoundary(contentType); //取分隔字符串
        findNextBoundary();     //将输入流指针定位到表单内容开始

        if (this.listener != null) {
            this.listener.onUploadStart(request);
        }
        processParts();
    }

    private void processParts() throws Exception {
        ContentDisposition disposition = nextDisposition();//取Content-Disposition 行

        while (!disposition.getName().equals("")) {//所有表单数据都有name="表单域名" 字段
            if (disposition.getFileName().equals("")) {//网页数据，没有 filename="" 字段
                storeVars(disposition.getName());
            } else {
                storeFile(disposition);
            }

            disposition = nextDisposition();
        }


    }

    private void storeVars(String name) throws IOException {
        skipEmptyLine();
        items.add(name, findNextBoundary());//一直读到下一个分隔字符串并将分隔字符串前面的内容返回

    }

    private void storeFile(ContentDisposition disposition) throws IOException {
        FileOutputStream out = null;
        try {
            File f = new File(this.saveDir + this.fileSeparator + this.getFileName(disposition.getFileName()));
            f.getParentFile().mkdirs();
            out = new FileOutputStream(f);
            skipEmptyLine();
            int bytesReaded = 0;
            boolean rnAddFlag = false;
            byte[] bytes = new byte[BUFFER_SIZE];

            Status st = null;

            if (this.useUploadStatus) {
                HttpSession sess = request.getSession(true);
                st = (Status) sess.getAttribute(EasyUpload.UPLOAD_STATUS);
                if (st == null) {
                    st = new Status();
                    sess.setAttribute(EasyUpload.UPLOAD_STATUS, st);
                }

                st.setContentLength(request.getContentLength());
                st.setCurrentFileIndex(files.size() + 1);
            }


            while ((bytesReaded = in.readLine(bytes, 0, bytes.length)) != -1) {
                this.totalContent += bytesReaded;

                if (bytes[0] == '-' && bytes[1] == '-' && bytes[2] == '-' && bytesReaded < 500) {//如果读到了分隔字符串，即文件内容读取完毕
                    String line = new String(bytes, 0, bytesReaded, charset);
                    if (line.startsWith(boundary)) {
                        break;
                    }
                }


                if (rnAddFlag) {
                    out.write('\r');
                    out.write('\n');
                    rnAddFlag = false;
                }

                if (st != null) {
                    st.setSpendTime(System.currentTimeMillis() - st.getBeginTime());
                    st.setBytesReaded(st.getBytesReaded() + bytesReaded);
                    //System.out.println(st.toString());
                }

                if (bytesReaded > 2 && bytes[bytesReaded - 2] == '\r' && bytes[bytesReaded - 1] == '\n') {
                    bytesReaded = bytesReaded - 2;
                    rnAddFlag = true;
                }

                out.write(bytes, 0, bytesReaded);
            }

            System.out.println("成功保存文件: " + f.getAbsolutePath());
            files.add(f);
        } finally {
            if (out != null) {
                out.close();
            }
            isOver();
        }

    }

    private String getBoundary(String type) {
        String marker = "boundary=";
        int bIndex = type.indexOf(marker) + marker.length();
        marker = "--" + type.substring(bIndex);
        return marker;
    }

    private String findNextBoundary() throws IOException {
        String line = msr.readLine();
        String b = "";
        while (line != null && !line.startsWith(boundary)) {
            b += line + "\r\n";
            line = msr.readLine();
        }

        if (b.length() > 0) {
            b.substring(0, b.length() - 2);
        }
        return b;
    }

    private ContentDisposition nextDisposition() throws Exception {
        String line = msr.readLine();
        while (line != null && !line.equals("") && !line.toLowerCase().startsWith("content-disposition:")) {
            line = msr.readLine();
        }

        ContentDisposition pos = new ContentDisposition();
        if (line == null || !line.toLowerCase().startsWith("content-disposition:")) {
            return pos;
        }

        String name = getDispositionName(line);
        pos.setName(name);

        String fileName = getDispositionFileName(line);
        pos.setFileName(fileName);
        return pos;
    }

    private String getDispositionName(String line) {
        String search = "name=\"";
        int nIndex = line.toLowerCase().indexOf(search);
        if (nIndex == -1) {
            return "";
        }
        nIndex += search.length();
        String disName = line.substring(nIndex, line.indexOf("\"", nIndex + 1));
        return disName;
    }

    private String getDispositionFileName(String line) {
        String search = "filename=\"";
        int fIndex = line.toLowerCase().indexOf(search);
        if (fIndex == -1) {
            return "";
        }
        fIndex += search.length();
        String fName = line.substring(fIndex, line.indexOf("\"", fIndex));
        fName = fName.substring(fName.lastIndexOf(fileSeparator) + 1);
        return fName;
    }

    private void skipEmptyLine() throws IOException {//使输入流指针跳过空白行
        String line = msr.readLine();
        while (line != null && !line.equals("")) {
            line = msr.readLine();
        }
    }

    private void isOver() {
        if (this.totalContent >= this.contentLength) {
            if (this.listener != null) {
                this.listener.onUploadComplete(request);
            }
            if (this.useUploadStatus) {//一旦上传完毕，删除状态对象
                this.request.getSession(true).removeAttribute(EasyUpload.UPLOAD_STATUS);
            }
        }
    }

    class MultipartStreamReader {

        public MultipartStreamReader() {
        }

        public String readLine() throws IOException {
            String line = null;
            byte[] bytes = new byte[BUFFER_SIZE];
            int byteReaded = in.readLine(bytes, 0, BUFFER_SIZE);
            if (byteReaded == -1) {
                return null;
            } else {
                totalContent += byteReaded;

                line = new String(bytes, 0, byteReaded, charset);
                if (useUploadStatus) {
                    HttpSession sess = request.getSession(true);
                    Status s = (Status) sess.getAttribute(EasyUpload.UPLOAD_STATUS);
                    if (s != null) {
                        s.setBytesReaded(s.getBytesReaded() + byteReaded);
                    }
                }
            }

            if (line.endsWith("\r\n")) {
                line = line.substring(0, line.length() - 2);
            }

            isOver();

            return line;
        }
    }

    class ContentDisposition {

        private String name;
        private String fileName;

        public ContentDisposition() {
            name = new String();
            fileName = new String();
        }

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        String getFileName() {
            return fileName;
        }

        void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    private class Param implements Parameter {

        private HashMap values;

        public Param() {
            values = new HashMap();
        }

        public void add(String name, String value) {
            if (values.get(name) == null) {
                values.put(name, new LinkedList());
            }
            LinkedList vs = (LinkedList) values.get(name);
            vs.add(value);
        }

        public String getParameter(String name) {
            LinkedList vs = (LinkedList) values.get(name);
            if (vs == null) {
                return null;
            }
            if (vs.size() <= 0) {
                return null;
            }
            return (String) vs.get(0);
        }

        public String[] getParameterValues(String name) {
            LinkedList vs = (LinkedList) values.get(name);
            if (vs == null) {
                return null;
            }
            String[] v = new String[vs.size()];
            for (int i = 0; i < vs.size(); i++) {
                v[i] = (String) vs.get(i);
            }
            return v;
        }

        public String[] getParameterNames() {
            String[] names = new String[values.size()];
            Iterator it = values.keySet().iterator();
            int i = 0;
            while (it.hasNext()) {
                names[i] = (String) it.next();
                i++;
            }
            return names;
        }
    }//end Param class

    private class Status implements UploadStatus {

        private long bytesReaded;
        private long contentLength;
        private int currentFileIndex;
        private long beginTime;
        private long spendTime;

        public Status() {
            this.bytesReaded = 0L;
            this.contentLength = 0L;
            this.currentFileIndex = 0;
            this.beginTime = System.currentTimeMillis();
            this.spendTime = 0L;
        }

        public long getBytesReaded() {
            return this.bytesReaded;
        }

        public void setBytesReaded(long bytesReaded) {
            this.bytesReaded = bytesReaded;
        }

        public long getContentLength() {
            return this.contentLength;
        }

        public void setContentLength(long contentLength) {
            this.contentLength = contentLength;
        }

        public int getCurrentFileIndex() {
            return this.currentFileIndex;
        }

        public void setCurrentFileIndex(int currentFileIndex) {
            this.currentFileIndex = currentFileIndex;
        }

        public long getBeginTime() {
            return this.beginTime;
        }

        public float getTransferRate() {//  KB/S
            return this.bytesReaded / 1024F / ((System.currentTimeMillis() - this.beginTime) / 1000F);
        }

        public long getSpendTime() {
            return this.spendTime;
        }

        public void setSpendTime(long spendTime) {
            this.spendTime = spendTime;
        }

        @Override
        public String toString() {
            String s = "总长度：" + String.valueOf(this.contentLength) + "字节";
            s += "; 已花时间：" + String.valueOf(this.spendTime / 1000F) + "秒";
            s += "; 当前速率：" + String.valueOf(this.getTransferRate()) + "KB/S";
            s += "; 正在读取第 " + String.valueOf(this.currentFileIndex) + "个文件";
            s += "; 已读 ：" + String.valueOf(this.bytesReaded) + "字节";
            s += "; 进度 :" + String.valueOf(((float) this.bytesReaded / this.contentLength) * 100) + "%";
            s += "; 剩余: " + String.valueOf((this.contentLength - this.bytesReaded) / 1024 / this.getTransferRate()) + "秒";
            return s;
        }
    }//end class Status
}
