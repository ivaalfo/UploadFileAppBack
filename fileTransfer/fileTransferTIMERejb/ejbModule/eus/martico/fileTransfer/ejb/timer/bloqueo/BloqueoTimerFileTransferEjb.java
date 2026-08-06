package eus.martico.fileTransfer.ejb.timer.bloqueo;

import javax.ejb.Local;

import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;

@Local
public interface BloqueoTimerFileTransferEjb {

	public boolean iniciar(BloqueoVO bloqueo);
	public boolean reiniciar();
	public boolean cancelar(BloqueoVO _bloqueo);
	public boolean cancelAll();
}
