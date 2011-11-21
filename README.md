Data fitting library
====================

This library provides fitting-style algorithms operating on
geometric data.

This library depends on my [matrix](https://github.com/kazocsaba/matrix) and
[geometry](https://github.com/kazocsaba/geometry) packages.

Using
-----

The library resides in the central Maven repository with group ID `hu.kazocsaba.math` and
artifact ID `fitting`. If you use a project management system which can fetch dependencies
from there, you can just add the library as a dependency. E.g. in Maven:

	<dependency>
		<groupId>hu.kazocsaba.math</groupId>
		<artifactId>geometry</artifactId>
		<version>a.b.c</version>
	</dependency>

You can also browse the [online javadoc](http://kazocsaba.github.com/fitting/apidocs/index.html).

Features
--------

LineFitter: finds the 2D/3D line containing a set of points.

PlaneFitter: finds the 3D plane containing a set of points.

LineIntersector: finds the 2D/3D/nD point lying on a set of lines.
