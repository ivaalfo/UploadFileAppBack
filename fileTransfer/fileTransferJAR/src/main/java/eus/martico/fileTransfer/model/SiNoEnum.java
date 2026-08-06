package eus.martico.fileTransfer.model;

public enum SiNoEnum {
	NO((byte)0),
	SI((byte)1),
	;

	private byte valor=-1;
	
	SiNoEnum(Byte value) {
		this.valor = value;
	}

	public static SiNoEnum valueOf(Byte value) {
		 switch(value){
	        case 1: return SiNoEnum.SI;
	        default:
	            return SiNoEnum.NO; 
	      }	
	}

	public byte value() {
		return this.valor;
	}
	public Character charValue() {
		 switch(this.valor){
	        case 1: return new Character('S');
	        default:
	            return new Character('N'); 
	      }	
	}
}
