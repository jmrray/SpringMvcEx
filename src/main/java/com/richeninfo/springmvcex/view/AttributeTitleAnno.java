/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.richeninfo.springmvcex.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author xiaolie
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeTitleAnno {
   String value();
   String index();
   String showText() default "";
}
