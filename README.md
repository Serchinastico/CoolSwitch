# CoolSwitch

CoolSwitch is a custom view for Android with an awesome reveal animation with support for Android versions >2.3.+ 

![alt tag](https://raw.github.com/Serchinastico/CoolSwitcher/master/demo.gif)

# Usage

* 1. Include the switch view in your layout as usual, specifying the views that will be displayed when the switch changes its state. If any of the views is undefined then the animation won't play.
```xml
<com.serchinastico.coolswitch.CoolSwitch
	android:id="@+id/cool_switch_foo"
	android:layout_height="35dp"
	android:layout_width="60dp"
	coolswitch:disabledView="@id/disabled_view_foo"
	coolswitch:enabledView="@id/enabled_view_foo"/>
```
* 2. Wrap your animated views in either a TargetFrameLayout or a TargetLinearLayout.
```xml
<com.serchinastico.coolswitch.TargetFrameLayout
		android:layout_width="match_parent"
		android:layout_height="match_parent">

	<LinearLayout
			android:id="@+id/disabled_view_foo"
			android:layout_width="match_parent"
			android:layout_height="match_parent"
			android:background="#FF0000"/>

	<LinearLayout
			android:id="@+id/enabled_view_foo"
			android:layout_width="match_parent"
			android:layout_height="match_parent"
			android:background="#00FF00"/>

</com.serchinastico.coolswitch.TargetFrameLayout>
```
# Import
You can easily include CoolSwitch in your project using your preferred build system. You just need to add the dependency from maven central, jcenter or your local artifact repository.
* Maven:
```xml
<dependency>
	<groupId>com.github.serchinastico</groupId>
	<artifactId>coolswitch</artifactId>
	<version>1.0.1</version>
	<type>aar</type>
</dependency>
```
* Gradle:
```groovy
dependencies {
	compile 'com.github.serchinastico:coolswitch:1.0.1'
}
```
# Acknowledgments

Víctor Pimentel (https://github.com/victorpimentel)

# License 
```
Copyright 2015 Sergio Gutiérrez Mota

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
