package eus.martico.fileTransfer.model;

public enum OkNOkEnum {
	UNKNOWN((byte)-1),
	OK((byte)1),
	NOK((byte)0),
	;

	private byte valor=-1;
	
	OkNOkEnum(Byte value) {
		this.valor = value;
	}

	public static OkNOkEnum valueOf(Byte value) {
		 switch(value){
	        case 0: return OkNOkEnum.NOK;
	        case 1: return OkNOkEnum.OK;
	        default:
	            return OkNOkEnum.UNKNOWN; 
	      }	
	}

	public byte value() {
		return this.valor;
	}

}
