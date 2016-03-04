/*
	Copyright 2015 Denis Prasetio
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package com.dotosoft.dotoquiz.command.generic;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;

import com.dotosoft.dotoquiz.tools.util.BeanUtils;

public class LoopCommand extends ChainBase {

	public boolean doWhile = false;
	public String checkKey;
	public int loopTime = 0;
	public String indexKey;

	public void setIndexKey(String indexKey) {
		this.indexKey = indexKey;
	}

	public void setDoWhile(boolean doWhile) {
		this.doWhile = doWhile;
	}

	public void setLoopTime(int loopTime) {
		this.loopTime = loopTime;
	}

	public void setCheckKey(String checkKey) {
		this.checkKey = checkKey;
	}

	@Override
	public boolean execute(Context context) throws Exception {
		
		boolean result = false;
		Integer index = (Integer) BeanUtils.getProperty(context, indexKey, 0);
		context.put(indexKey, index);
		
		boolean isLoopTime = (loopTime > 0);
		if (doWhile) {
			result = super.execute(context);
			if(isLoopTime) loopTime -= 1;
			context.put(indexKey, ++index);
		}
		while((isLoopTime && loopTime > 0) || BeanUtils.getProperty(context, checkKey) != null) {
			result = super.execute(context);
			if (isLoopTime) loopTime -= 1;
			if (result) break;
			context.put(indexKey, ++index);
		}
		return result;
	}

}