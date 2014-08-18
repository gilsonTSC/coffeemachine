package br.ufpb.dce.aps.coffeemachine.impl;

import net.compor.frameworks.jcf.api.Service;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;
import br.ufpb.dce.aps.coffeemachine.Messages;


public class CafeBranco extends CafePreto{
	
		
	public CafeBranco(ComponentsFactory factory){
		super(factory);
	}
	@Service
	public boolean VerificaDrinkBranco(){
		if(!VerificaDrinkPreto()){
			return false;
		}
		if (!this.factory.getCreamerDispenser().contains(1.2)) {
			this.factory.getDisplay().warn(Messages.OUT_OF_CREAMER);
			return false;
		}
		return true;
		
	}
	@Service
	public void liberaIngredientesCafeBranco(){
		liberaIngredientesCafePreto();
		this.factory.getCreamerDispenser().release(1.2);
	}
}
