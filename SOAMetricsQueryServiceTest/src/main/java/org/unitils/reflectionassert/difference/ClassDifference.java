/*******************************************************************************
 * Copyright (c) 2006, 2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.unitils.reflectionassert.difference;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ClassDifference extends Difference {

    private Class<?> leftClass;
    private Class<?> rightClass;

    /**
     * Creates a difference.
     *
     * @param message    a message describing the difference
     * @param leftValue  the left instance
     * @param rightValue the right instance
     */
    public ClassDifference(String message, Object leftValue, Object rightValue, Class<?> leftClass, Class<?> rightClass) {
        super(message, leftValue, rightValue);
        this.leftClass = leftClass;
        this.rightClass = rightClass;
    }

    public Class<?> getLeftClass() {
        return leftClass;
    }

    public Class<?> getRightClass() {
        return rightClass;
    }

    @Override
     public <T, A> T accept(DifferenceVisitor<T, A> visitor, A argument) {
        return visitor.visit(this, argument);
    }
}
