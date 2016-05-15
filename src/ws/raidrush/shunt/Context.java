/*!
 * Java Shunting-yard Implementierung
 * Copyright 2012 - droptable <murdoc@raidrush.org>
 *
 * Referenz: <http://en.wikipedia.org/wiki/Shunting-yard_algorithm>
 *
 * ---------------------------------------------------------------- 
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * <http://opensource.org/licenses/mit-license.php>
 */

package ws.raidrush.shunt;

import java.util.Map;
import java.util.HashMap;

public class Context 
{
	public Map<String, Function> functionTable;
	public Map<String, RSymbol>   constantTable;
	public Map<String, RSymbol>   symbolTable;
	
	public Context()
	{
		functionTable = new HashMap<String, Function>();
		constantTable = new HashMap<String, RSymbol>();
		symbolTable   = new HashMap<String, RSymbol>();
		
		// define PI
		//NOT Work?
//		constantTable.put("pi", new RSymbol(Math.PI, true));
//		constantTable.put("PI", new RSymbol(Math.PI, true));
//		constantTable.put("e", new RSymbol(Math.E));
	}
	
	public Function getFunction(String name) throws RuntimeError
	{
		if (!functionTable.containsKey(name))
			throw new RuntimeError("undefinierte funktion \"" + name + "\"");
		
		return functionTable.get(name);
	}
	
	public RSymbol getConstant(String name) throws RuntimeError
	{
		if (!constantTable.containsKey(name))
			throw new RuntimeError("undefinierte konstante \"" + name + "\"");
		
		return constantTable.get(name);
	}
	
	public RSymbol getSymbol(String name) throws RuntimeError 
  {
    if (!symbolTable.containsKey(name))
      throw new RuntimeError("undefiniertes symbol \"" + name + "\"");
      
    return symbolTable.get(name);
  }
	
	public Context setConstant(String name, RSymbol value)
	{
	  value.readonly = true;
	  return setSymbol(name, value);
	}
	
	public Context setFunction(String name, Function value)
	{
		functionTable.put(name, value);
		return this;
	}
	
	public Context setSymbol(String name, RSymbol value)
	{
	  symbolTable.put(name, value);
	  return this;
	}
}
