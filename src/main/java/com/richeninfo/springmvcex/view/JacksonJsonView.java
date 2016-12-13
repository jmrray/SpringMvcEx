/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.richeninfo.springmvcex.view;

import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.View;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author xiaolie
 */
public class JacksonJsonView implements View {
    private static final Log logger = LogFactory.getLog(JacksonJsonView.class);

    private String encode = "utf-8";
	private String contentType = "application/json";

    public JacksonJsonView() {
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
		response.setCharacterEncoding(encode);
		if(!response.containsHeader("Content-Type")) {
			response.setContentType(contentType + ";charset=" + encode);
		}
		PrintWriter pwriter = response.getWriter();
        ObjectMapper  objectMapper = new ObjectMapper();
        JsonGenerator generator = objectMapper.getJsonFactory().createJsonGenerator(pwriter);
        generator.writeObject(map);
		pwriter.flush();
	}
}
