package com.dlcat.common.entity;
import java.io.Serializable;

/**
 * 请求响应类
 * @author masai
 * @time 2017年4月13日 下午8:46:54
 */
public class DyResponse implements Serializable {
	private static final long serialVersionUID = 4022828639456713673L;
	public static final int OK = 200;
	public static final int ERROR = 100;
	public static final int LOGINERR = 250;
	private int status;
	private Object data;
	private Object description;

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getDescription() {
		return this.description;
	}

	public void setDescription(Object description) {
		this.description = description;
	}
}