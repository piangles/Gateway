package org.piangles.app.gateway;

import java.lang.annotation.Annotation;

import org.piangles.core.annotation.Description;
import org.piangles.gateway.requests.Endpoints;

public class TestAnnotation
{
	public static void main(String[] args) throws Exception
	{
		Enum<?> enm = Endpoints.SignUp;
		
		Description desc = enm.getClass().getField(enm.name()).getAnnotation(Description.class);
		if (desc != null)
		{
			System.out.println(desc.content());
		}
		
		 Annotation[] annotations = enm.getClass().getField(enm.name()).getAnnotations();
		for (int i=0; i < annotations.length; ++i) {
			System.out.println(annotations[i]);
		}
		
		System.out.println(enm.getClass().getDeclaredAnnotation(Description.class));
	}
}
