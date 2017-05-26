package com.dlcat.core.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.server.Request;

import com.dlcat.common.BaseController;
import com.dlcat.common.utils.DateUtil;
import com.dlcat.core.model.SysUser;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.PropKit;
import com.jfinal.upload.UploadFile;

public class CrudController extends BaseController {

	public void index() {
		render("/WEB-INF/views/img.html");
	}

	public void add() {
		
		
	}
	public void upload() throws IOException {
		SysUser user=getSessionAttr("user");
		
		UploadFile uploadFile = getFile();
		String name = uploadFile.getFileName();
		String suffix = name.substring(name.indexOf("."), name.length());

		String path = uploadFile.getUploadPath() + "/"+DateUtil.getCurrentDateStr()+"/" + user.getStr("name");
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		path += "/" + UUID.randomUUID().toString().replaceAll("-", "") + suffix;
		uploadFile.getFile().renameTo(new File(path));
 		renderNull();
	}

	/**
	 * @author:zhaozhongyuan
	 * @Description: 执行删除
	 * @param
	 * @return void
	 * @date 2017年4月19日 上午11:56:25
	 */
	public void del() {

	}

	/**
	 * @author:zhaozhongyuan
	 * @Description: 对form表单执行真正的更新操作
	 * @param
	 * @return void
	 * @date 2017年4月19日 上午11:55:12
	 */
	public void update() {

		renderText("usa");

	}

	public void query() {

		render("userAdd.html");

	}

	public void img() {

		render("img.html");

	}
}
