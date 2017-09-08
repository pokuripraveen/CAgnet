package com.unique.agent.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by praveenpokuri on 09/08/17.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomScope {
}