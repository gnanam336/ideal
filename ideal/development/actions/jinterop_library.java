/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import javax.annotation.Nullable;
import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;

public class jinterop_library implements value {

  public static final base_string JINTEROP_NAME = new base_string("jinterop");

  private static jinterop_library instance;

  private analysis_context context;

  private principal_type java_package;
  private principal_type builtins_package;
  private principal_type lang_package;

  private principal_type int_type;
  private principal_type boolean_type;
  private principal_type char_type;

  private principal_type object_type;
  private principal_type string_type;

  private principal_type javax_package;
  private principal_type annotation_package;
  private principal_type nullable_type;

  private principal_type machine_elements_package;
  private principal_type runtime_util_class;

  private final dictionary<principal_type, principal_type> primitive_mapping;
  private final dictionary<principal_type, simple_name> wrapper_mapping;

  public jinterop_library(analysis_context context, principal_type top) {
    this.context = context;

    java_package = get_type(top, "java");
    builtins_package = get_type(java_package, "builtins");
    lang_package = get_type(java_package, "lang");

    int_type = get_type(builtins_package, "int");
    boolean_type = get_type(builtins_package, "boolean");
    char_type = get_type(builtins_package, "char");

    object_type = get_type(lang_package, "Object");
    string_type = get_type(lang_package, "String");

    javax_package = get_type(top, "javax");
    annotation_package = get_type(javax_package, "annotation");
    nullable_type = get_type(annotation_package, "Nullable");

    primitive_mapping = new list_dictionary<principal_type, principal_type>();
    wrapper_mapping = new list_dictionary<principal_type, simple_name>();

    common_library library = common_library.get_instance();

    add_mapping(library.boolean_type(), boolean_type(), "Boolean");
    add_mapping(library.character_type(), char_type(), "Character");
    add_mapping(library.integer_type(), int_type(), "Integer");
    add_mapping(library.nonnegative_type(), int_type(), "Integer");
    add_mapping(library.void_type(), library.void_type(), "Void");

    assert instance == null;
    instance = this;
  }

  private void add_mapping(principal_type ideal_type, principal_type java_type,
      String java_wrapper_name) {
    primitive_mapping.put(ideal_type, java_type);
    wrapper_mapping.put(ideal_type, simple_name.make(java_wrapper_name));
  }

  public boolean is_mapped(principal_type the_type) {
    return primitive_mapping.contains_key(the_type);
  }

  public @Nullable principal_type map_to_primitive(principal_type the_type) {
    return primitive_mapping.get(the_type);
  }

  public @Nullable simple_name map_to_wrapper(principal_type the_type) {
    return wrapper_mapping.get(the_type);
  }

  public principal_type int_type() {
    return int_type;
  }

  public principal_type boolean_type() {
    return boolean_type;
  }

  public principal_type char_type() {
    return char_type;
  }

  public principal_type object_type() {
    return object_type;
  }

  public principal_type string_type() {
    return string_type;
  }

  public principal_type builtins_package() {
    return builtins_package;
  }

  public principal_type lang_package() {
    return lang_package;
  }

  public principal_type nullable_type() {
    return nullable_type;
  }

  public principal_type machine_elements_package() {
    if (machine_elements_package == null) {
      principal_type ideal_type = common_library.get_instance().ideal_namespace();
      principal_type machine_type = get_type(ideal_type, "machine");
      machine_elements_package = get_type(machine_type, "elements");
    }
    return machine_elements_package;
  }

  public principal_type runtime_util_class() {
    if (runtime_util_class == null) {
      runtime_util_class = get_type(machine_elements_package(), "runtime_util");
    }
    return runtime_util_class;
  }

  // TODO: this code is duplicated, factor out.
  private principal_type get_type(principal_type parent, String sname) {
    simple_name name = simple_name.make(new base_string(sname));
    readonly_list<type> types = action_utilities.lookup_types(context, parent, name);
    if (types.size() != 1) {
      utilities.panic("Got " + types.size() + " for " + sname);
      assert types.size() == 1;
    }
    return (principal_type) types.get(0);
  }

  // TODO: use features...
  public static boolean is_enabled() {
    return instance != null;
  }

  // TODO: use features...
  public static jinterop_library get_instance() {
    assert instance != null;
    return instance;
  }
}
