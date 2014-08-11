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
		if (!this.factory.getWaterDispenser().contains(1.2)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_WATER);
			return false;
		}
		if (!this.factory.getCoffeePowderDispenser().contains(1.2)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_COFFEE_POWDER);
			return false;
		}
		return true;
	}
	
	@Service
	public void liberaIngredientesCafePreto(){
		this.factory.getCoffeePowderDispenser().release(1.2);
		this.factory.getWaterDispenser().release(1.2);
	}
	@Service
	public void LiberandoBebida(){
		this.factory.getDisplay().info(Messages.RELEASING);
		this.factory.getCupDispenser().release(1);
		this.factory.getDrinkDispenser().release(1.2);
		this.factory.getDisplay().info(Messages.TAKE_DRINK);
	} 
}
