package eus.fileTransfer.test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PruebaArrayLong {

	public static void main(String[] args) {
		long[] al = {2,4,5,6,7896};
		arrayToLong(al);
		System.exit(0);
	}

	private static Long[] arrayToLong(long[] _arrl) {
		if (_arrl==null)
			return null;
		else if(_arrl.length==0)
			return new Long[]{};
		else {
			//Long[] longObjects = ArrayUtils.toObject(_lista);
			List<Long> list = Arrays.stream(_arrl).boxed().collect(Collectors.toList());
			Long[] longObjects = list.toArray(new Long[list.size()]);
			for(Long l:longObjects) {
				System.out.println(l);
			}
			return longObjects;
		}
	}

}
