package br.ufpb.dce.aps.coffeemachine.impl;

import net.compor.frameworks.jcf.api.ComporFacade;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachine;
import br.ufpb.dce.aps.coffeemachine.Coin;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;

public class MyCoffeeMachine extends ComporFacade implements CoffeeMachine{

	private ComponentsFactory factory;
	private int divisao, resto;
	
	public MyCoffeeMachine(ComponentsFactory factory){
		this.factory = factory;
		this.factory.getDisplay().info("Insert coins and select a drink!");
		this.divisao = 0;
		this.resto = 0;
	}

	public void insertCoin(Coin dime) {
		this.divisao += dime.getValue() / 100;
		this.resto += dime.getValue() % 100;
		this.factory.getDisplay().info("Total: US$ " + this.divisao + "." + this.resto);
	}
}