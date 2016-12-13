/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.stringtree.json;

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
public @interface JSONTitleAnno {
    String value();
}
