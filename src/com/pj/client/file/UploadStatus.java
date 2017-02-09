
package com.pj.client.file;

/**
 *
 * @author pengju
 */
public interface UploadStatus {
	public long  getBytesReaded();//返回已读字节
	public long  getContentLength();//总长度，字节
	public int   getCurrentFileIndex();//当前正在上传第几个文件
	public long  getBeginTime();//开始上传时间，返回毫秒数
	public float getTransferRate();//传输速率，KB/S
	public long  getSpendTime();//已花时间，返回毫秒数

}
