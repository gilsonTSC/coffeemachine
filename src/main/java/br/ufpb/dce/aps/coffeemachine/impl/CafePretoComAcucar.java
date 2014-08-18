package br.ufpb.dce.aps.coffeemachine.impl;

import net.compor.frameworks.jcf.api.Service;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Messages;

public class CafePretoComAcucar extends CafePreto {
	
	public CafePretoComAcucar(ComponentsFactory factory){
		super(factory);
	}
	@Service
	public boolean VerificaDrinkPretoComAcucar(){
		if(!VerificaDrinkPreto()){
			return false;
		}
		if (!this.factory.getSugarDispenser().contains(5)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_SUGAR);
			return false;
		}
		return true;
	}
	@Service
	public void LiberaIngredientesCafePretoComAcucar() {
		liberaIngredientesCafePreto();
		factory.getSugarDispenser().release(5);	
	}
}
