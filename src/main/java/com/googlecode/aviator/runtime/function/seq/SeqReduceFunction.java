/**
 * Copyright (C) 2010 dennis zhuang (killme2008@gmail.com)
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 **/
package com.googlecode.aviator.runtime.function.seq;

import java.lang.reflect.Array;
import java.util.Map;
import com.googlecode.aviator.exception.FunctionNotFoundException;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorFunction;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;


/**
 * reduce(col,fun,init) function to reduce seq with function and a initial value value
 *
 * @author dennis
 *
 */
public class SeqReduceFunction extends AbstractFunction {

  @Override
  public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1,
      final AviatorObject arg2, final AviatorObject arg3) {
    Object first = arg1.getValue(env);
    AviatorFunction fun = FunctionUtils.getFunction(arg2, env, 2);
    if (fun == null) {
      throw new FunctionNotFoundException(
          "There is no function named " + ((AviatorJavaType) arg2).getName());
    }
    if (first == null) {
      throw new NullPointerException("null seq");
    }
    AviatorObject result = arg3;
    Class<?> clazz = first.getClass();

    if (Iterable.class.isAssignableFrom(clazz)) {
      for (Object obj : (Iterable<?>) first) {
        result = fun.call(env, result, AviatorRuntimeJavaType.valueOf(obj));
      }
    } else if (Map.class.isAssignableFrom(clazz)) {
      for (Object obj : ((Map<?, ?>) first).entrySet()) {
        result = fun.call(env, result, AviatorRuntimeJavaType.valueOf(obj));
      }
    } else if (clazz.isArray()) {
      int length = Array.getLength(first);
      for (int i = 0; i < length; i++) {
        Object obj = Array.get(first, i);
        result = fun.call(env, result, AviatorRuntimeJavaType.valueOf(obj));
      }
    } else {
      throw new IllegalArgumentException(arg1.desc(env) + " is not a seq");
    }

    return result;
  }


  @Override
  public String getName() {
    return "reduce";
  }

}
