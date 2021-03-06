/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.pos.service.bean;

import java.io.Serializable;

/**
 * 
 * @author byp
 * @version $Id: NeedMoreEvidMessage.java, v 0.1 2015年3月16日 下午6:35:33 byp Exp $
 */
public class NeedMoreEvidMessage implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -3536588346251645010L;

	private String addiSpec;
    
    private String additPhase;
    /**
     * 
     */
    public NeedMoreEvidMessage() {
    }
    public String getAddiSpec() {
        return addiSpec;
    }
    public NeedMoreEvidMessage setAddiSpec(String addiSpec) {
        this.addiSpec = addiSpec;
        return this;
    }
    public String getAdditPhase() {
        return additPhase;
    }
    public NeedMoreEvidMessage setAdditPhase(String additPhase) {
        this.additPhase = additPhase;
        return this;
    }

    public String toString(){
        return new String("\"additspec\":\"" + addiSpec + "\","
                + "\"additphase\":\"" + additPhase +"\"").replaceAll("[\\t\\n\\r]", "");
    }
}
