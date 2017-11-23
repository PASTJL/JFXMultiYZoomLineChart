/*
 * Copyright 2017 Jean-Louis Pasturel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
module org.jlp.javafx {
	requires javafx.base;

	requires transitive javafx.graphics;
	requires javafx.controls;
	requires java.base;

	// for scenic View
	requires javafx.web;
	requires javafx.fxml;
	requires jdk.attach;

	exports org.jlp.javafx;
	exports org.jlp.javafx.example;
	exports org.jlp.javafx.ext;

}