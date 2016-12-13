/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.richeninfo.springmvcex;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author xiaolie
 */
public class ClientReturnUtils {
    
    public static Map<String, Object> createReturnMap(Boolean result) {
        Map map = new HashMap();
        map.put("success", result);
        return map;
    }

    public static Map<String, Object> createReturnMap(Boolean result, String msg) {
        Map map = createReturnMap(result);
        if (StringUtils.isNotBlank(msg)) {
            map.put("message", msg);
        }
        return map;
    }

    public static Map<String, Object> createReturnMap(Boolean result, String msg, Object data) {
        Map map = createReturnMap(result, msg);
        if (data != null) {
            map.put("data", data);
        }
        return map;
    }
}
