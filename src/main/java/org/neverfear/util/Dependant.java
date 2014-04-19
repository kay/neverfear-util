package org.neverfear.util;

import java.util.Collection;

public interface Dependant<T extends Dependant<T>> {

	Collection<T> dependencies();
}
