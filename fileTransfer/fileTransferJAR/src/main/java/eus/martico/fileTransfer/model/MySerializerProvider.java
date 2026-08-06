package eus.martico.fileTransfer.model;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;

public class MySerializerProvider extends SerializerProvider {

	@Override
	public WritableObjectId findObjectId(Object arg0, ObjectIdGenerator<?> arg1) {
		// Auto-generated method stub
		return null;
	}

	@Override
	public Object includeFilterInstance(BeanPropertyDefinition arg0, Class<?> arg1) throws JsonMappingException {
		// Auto-generated method stub
		return null;
	}

	@Override
	public boolean includeFilterSuppressNulls(Object arg0) throws JsonMappingException {
		// Auto-generated method stub
		return false;
	}

	@Override
	public JsonSerializer<Object> serializerInstance(Annotated arg0, Object arg1) throws JsonMappingException {
		// Auto-generated method stub
		return null;
	}

}
