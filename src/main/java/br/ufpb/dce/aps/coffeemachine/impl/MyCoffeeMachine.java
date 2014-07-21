package br.ufpb.dce.aps.coffeemachine.impl;

import net.compor.frameworks.jcf.api.ComporFacade;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachine;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachineException;
import br.ufpb.dce.aps.coffeemachine.Coin;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Messages;

public class MyCoffeeMachine extends ComporFacade implements CoffeeMachine{

	private ComponentsFactory factory;
	private int dolares, centavos;
	
	public MyCoffeeMachine(ComponentsFactory factory){
		this.factory = factory;
		this.factory.getDisplay().info("Insert coins and select a drink!");
		this.dolares = 0;
		this.centavos = 0;
	}

	public void insertCoin(Coin dime) {
		if(dime == null){
			throw new CoffeeMachineException("Coin null");
		}
		this.dolares += dime.getValue() / 100;
		this.centavos += dime.getValue() % 100;
		this.factory.getDisplay().info("Total: US$ " + this.dolares + "." + this.centavos);
	}

	public void cancel() {
		if(this.dolares == 0 && this.centavos == 0){
			throw new CoffeeMachineException("Não tem moedas inseridas");
		}
		this.factory.getDisplay().warn(Messages.CANCEL_MESSAGE);
		this.factory.getCashBox().release(Coin.halfDollar);
		this.factory.getDisplay().info(Messages.INSERT_COINS_MESSAGE);
	}
}