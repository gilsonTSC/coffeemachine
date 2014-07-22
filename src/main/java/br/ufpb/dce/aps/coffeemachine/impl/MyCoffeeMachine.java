package br.ufpb.dce.aps.coffeemachine.impl;

import net.compor.frameworks.jcf.api.ComporFacade;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachine;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachineException;
import br.ufpb.dce.aps.coffeemachine.Coin;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Messages;

public class MyCoffeeMachine extends ComporFacade implements CoffeeMachine{

	private ComponentsFactory factory;
	private int dolares, centavos, cont;
	private Coin[] moedas;
	
	public MyCoffeeMachine(ComponentsFactory factory){
		this.factory = factory;
		this.factory.getDisplay().info("Insert coins and select a drink!");
		this.dolares = 0;
		this.centavos = 0;
		this.moedas = new Coin[100];
		this.cont = 0;
	}

	public void insertCoin(Coin dime) {
		if(dime == null){
			throw new CoffeeMachineException("Coin null");
		}
		this.moedas[++this.cont] = dime;
		this.dolares += dime.getValue() / 100;
		this.centavos += dime.getValue() % 100;
		this.factory.getDisplay().info("Total: US$ " + this.dolares + "." + this.centavos);
	}

	public void cancel() {
		if(this.dolares == 0 && this.centavos == 0){
			throw new CoffeeMachineException("Não tem moedas inseridas");
		}
		this.factory.getDisplay().warn(Messages.CANCEL_MESSAGE);
		if(this.moedas.length > 2){
			Coin[] c = Coin.reverse();
			for(int i = 0; i < this.moedas.length; i++){
				this.factory.getCashBox().release(this.moedas[i]);
			}
			for(int i = 0; i < c.length; i++){
				this.factory.getCashBox().release(c[i]);
			}
		}
		this.factory.getDisplay().info(Messages.INSERT_COINS_MESSAGE);
	}
}