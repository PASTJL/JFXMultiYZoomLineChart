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
open module org.jlp.javafx {
	exports org.jlp.javafx;
	exports org.jlp.javafx.example;
	exports org.jlp.javafx.richview;
	exports org.jlp.javafx.datas;
	exports org.jlp.javafx.common;
	exports org.jlp.javafx.ext;

	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.graphics;
	requires java.base;
	requires java.desktop;
}