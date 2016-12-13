/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.richeninfo.springmvcex.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author xiaolie
 */
public abstract class MultiActionViewController extends MultiActionController {
    
	protected String view;
    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    protected Map<String, Object> getRequestParameters(HttpServletRequest request) {
        Map<String, Object> paramMap = new HashMap();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement().toString();
            String parameter = request.getParameter(name);
            paramMap.put(name, parameter);
        }
        return paramMap;
    }
}
