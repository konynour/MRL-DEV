/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.myrobotlab.service;

import org.myrobotlab.framework.Service;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

/**
 * Tensorflow - More info at : https://www.tensorflow.org/install/install_java
 * Currently only supported on windows / linux 64 / macosx no ARM support. (yet)
 * 
 * @author kwatters
 *
 */
public class Tensorflow extends Service {

  private static final long serialVersionUID = 1L;

  public Tensorflow(String name, String id) {
    super(name, id);
  }

  public static void main(String[] args) throws Exception {
    // Test code taken directly from the tensorflow webpage to verify that the
    // libraries have loaded as expected.
    try (Graph g = new Graph()) {
      final String value = "Hello from " + TensorFlow.version();

      // Construct the computation graph with a single operation, a constant
      // named "MyConst" with a value "value".
      try (Tensor t = Tensor.create(value.getBytes("UTF-8"))) {
        // The Java API doesn't yet include convenience functions for adding
        // operations.
        g.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType()).setAttr("value", t).build();
      }

      // Execute the "MyConst" operation in a Session.
      try (Session s = new Session(g); Tensor output = s.runner().fetch("MyConst").run().get(0)) {
        System.out.println(new String(output.bytesValue(), "UTF-8"));
      }
    }
  }
}