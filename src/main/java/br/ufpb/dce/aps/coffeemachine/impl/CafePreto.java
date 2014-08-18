package br.ufpb.dce.aps.coffeemachine.impl;

import net.compor.frameworks.jcf.api.Service;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Messages;

public class CafePreto extends Cafe{
	
	public ComponentsFactory factory;
	
	
	public CafePreto(ComponentsFactory factory){
		super();
		this.factory = factory;
	}
	
	@Service	
	public boolean VerificaDrinkPreto(){
	
		if (!this.factory.getCupDispenser().contains(1)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_CUP);
			return false;
		}
		if (!this.factory.getWaterDispenser().contains(100)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_WATER);
			return false;
		}
		if (!this.factory.getCoffeePowderDispenser().contains(15)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_COFFEE_POWDER);
			return false;
		}
		return true;
	}
	@Service
	public void liberaIngredientesCafePreto(){
		this.factory.getCoffeePowderDispenser().release(15);
		this.factory.getWaterDispenser().release(100);
	}
	@Service
	public void LiberandoBebida(){
		this.factory.getDisplay().info(Messages.RELEASING);
		this.factory.getCupDispenser().release(1);
		this.factory.getDrinkDispenser().release(100);
		this.factory.getDisplay().info(Messages.TAKE_DRINK);
	} 
}
