package br.ufpb.dce.aps.coffeemachine.impl;

import net.compor.frameworks.jcf.api.Service;
import br.ufpb.dce.aps.coffeemachine.ComponentsFactory;

public class CafeBrancoComAcucar extends CafePreto {
	
	public CafeBrancoComAcucar(ComponentsFactory factory){
		super(factory);
	}
	@Service
	public boolean VerificaDrinkBrancoComAcucar(){
		if(!VerificaDrinkPreto()){
			return false;
		}
		factory.getCreamerDispenser().contains(2.0); 
		factory.getSugarDispenser().contains(5.0);
		return true;
		
	}
	@Service
	public void liberaIngredientesCafeBrancoComAcucar(){
		liberaIngredientesCafePreto();
		this.factory.getCreamerDispenser().release(2);
		factory.getSugarDispenser().release(5);
	}
}
