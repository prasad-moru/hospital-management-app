package com.hospital.management.validation;
import java.util.*;
/** Field-level validation errors with a read-only public view. */
public final class ValidationResult{private final Map<String,String> errors=new LinkedHashMap<>();public void addError(String f,String m){errors.putIfAbsent(f,m);}public boolean hasErrors(){return!errors.isEmpty();}public Map<String,String> getErrors(){return Collections.unmodifiableMap(errors);}public String getError(String f){return errors.get(f);}}
