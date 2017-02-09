/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pj.client.core.invokers;

import com.pj.client.core.ServiceResolver;
import com.pj.client.core.resolvers.RestURIResolver;
import com.pj.web.res.Config;

/**
 *
 * @author luzhenwen
 */
public class JSONRestURIInvoker extends JSONInvoker{

    @Override
    public String getResolverClassName() {
        return Config.getConfig(ServiceResolver.CONF_CLASS_REST, RestURIResolver.class.getName()); //To change body of generated methods, choose Tools | Templates.
    }
    
}
