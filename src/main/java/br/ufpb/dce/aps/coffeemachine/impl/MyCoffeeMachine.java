package br.ufpb.dce.aps.coffeemachine.impl;

import net.compor.frameworks.jcf.api.ComporFacade;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachine;
import br.ufpb.dce.aps.coffeemachine.CoffeeMachineException;
import br.ufpb.dce.aps.coffeemachine.Coin;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Drink;
import br.ufpb.dce.aps.coffeemachine.Messages;

public class MyCoffeeMachine extends ComporFacade implements CoffeeMachine{

	private ComponentsFactory factory;
	private int dolares, centavos, cont;
	private Coin[] moedas;
	private Drink drink;
	
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
		this.factory.getDisplay().warn(Messages.CANCEL);
		if(this.moedas.length > 2){
			Coin[] c = Coin.reverse();
			for(int i = 0; i < this.moedas.length; i++){
				this.factory.getCashBox().release(this.moedas[i]);
			}
			for(int i = 0; i < c.length; i++){
				this.factory.getCashBox().release(c[i]);
			}
		}
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}

	public void select(Drink drink) {
		
		this.factory.getCupDispenser().contains(1);
		this.factory.getWaterDispenser().contains(1.2);
		this.factory.getCoffeePowderDispenser().contains(1.2);
		
		this.factory.getDisplay().info(Messages.MIXING);
		this.factory.getCoffeePowderDispenser().release(1.2);
		this.factory.getWaterDispenser().release(1.2);
		
		this.factory.getDisplay().info(Messages.RELEASING);
		this.factory.getCupDispenser().release(1);
		this.factory.getDrinkDispenser().release(1.2);
		this.factory.getDisplay().info(Messages.TAKE_DRINK);
		this.factory.getDisplay().info(Messages.INSERT_COINS);
	}
}