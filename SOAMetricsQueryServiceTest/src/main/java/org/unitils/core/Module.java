/*******************************************************************************
 * Copyright (c) 2006, 2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.unitils.core;

import java.util.Properties;

/**
 * A type for modules that offer services to tests.
 * Before a module is used, {@link #init} will be called so that it can initialize itself. After initialization,
 * {@link #getTestListener()} will be called, so that the module can create a callback that can plug into
 * the test exucution sequence. See {@link TestListener} javadoc for more info.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface Module {


    /**
     * Initializes the module with the given configuration settings.
     *
     * @param configuration The config, not null
     */
    void init(Properties configuration);
    
    
    /**
     * Gives the module the opportunity to performs initialization that
     * can only work after all other modules have been initialized  
     */
    void afterInit();


    /**
     * Creates the test listener for this module.
     *
     * @return The test listener, not null
     */
    TestListener getTestListener();

}
