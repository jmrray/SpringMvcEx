package com.richeninfo.springmvcex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationHelper implements ApplicationContextAware {
	
	private static ApplicationHelper helper = new ApplicationHelper();
    Log logger = LogFactory.getLog(ApplicationHelper.class);

    private ApplicationHelper() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ApplicationHelper()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
	
	public static ApplicationHelper getInstance() {
		return helper;
	}
	
	private ApplicationContext context; 

	public ApplicationContext getContext() {
		return context;
	}
	
	public Object getBean(String beanName){
		Object obj =getContext().getBean(beanName);
		return obj;
	}


	public void setContext(ApplicationContext context) {
		this.context = context;
	}

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        System.out.println("ApplicationHelper.setApplicationContext:" + ac);
        logger.info("ApplicationHelper.setApplicationContext:" + ac);
        getInstance().context = ac;
    }
		
	
}
