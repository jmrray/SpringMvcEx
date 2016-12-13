package com.richeninfo.springmvcex.view;

import java.io.PrintWriter;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import org.stringtree.json.JSONWriter;

/////////////////////////////////////////////////////////////////////////////
// 这个文件是 anaplatform 项目的一部分
//
// Copyright (c) 2008 RichenInfo
/////////////////////////////////////////////////////////////////////////////
/**
 * Created by IntelliJ IDEA.
 * User: DSON
 * Date: 2010-11-18
 * Time: 17:21:43
 * @author	DSONet (dsonet@msn.com)
 * @version $Id: JsonView.java 54141 2011-11-11 04:23:29Z shw $
 */
public class JsonView implements View {

	private String encode = "utf-8";
	private String contentType = "application/json";

	/** Creates a new instance of JsonView */
	public JsonView() {
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	@Override
	public void render(Map map, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONWriter jwriter = new JSONWriter(false).setTranslateNull(true);
		response.setCharacterEncoding(encode);
		if(!response.containsHeader("Content-Type")) {
			response.setContentType(contentType + ";charset=" + encode);
		}
		PrintWriter pwriter = response.getWriter();
		pwriter.print(jwriter.write(map.size() == 1 && map.containsKey("array") ? map.remove("array") : map));
		pwriter.flush();
	}
}
