package br.ufpb.dce.aps.coffeemachine.impl;


import net.compor.frameworks.jcf.api.ComporFacade;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachine;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;

public class MyCoffeeMachine extends ComporFacade implements CoffeeMachine{
	
 
	 public MyCoffeeMachine(ComponentsFactory factory){
		 factory.getDisplay().info("Insert coins and select a drink!");
	 }
}
