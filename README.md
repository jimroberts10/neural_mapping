# neural_mapping
Converts JSON CATMAID data into its own java class

RECENTLY ADDED- tool for viewing neuron data in 3D.

Controls: 
-<b>W Q S E</b> for moving Forward, Left, Backward, Right, respectively
-KeyPad <b>^ < v ></b> for turning Up, Left, Down, Right, respectively
-<b>A D</b> to rotate your frame of reference Clockwise/CounterClockwise
-<b>PgUp</b> to (continuously) zoom in, <b>PgDown</b> to zoom out

Still a work in progress, running the code should start viewing the java applet (though if you try to load too much data at once, rather than lagging the program will fail too launch as the refresh rate is too fast for updates. ..this will be fixed in the next update).

On launch, the image will probably look like this.
<img src ="http://teamjimroberts.com/wp-content/uploads/2016/02/readme_coordinates.png"/>

Zooming out (PgDn), or looking up and right ([^, >] on d-pad), will show the neuron that you can look at and move around. 

<img src = "http://teamjimroberts.com/wp-content/uploads/2016/02/readme_neuron.png"/>

The tool is powerful, though needs a few bug fixes and an intuitive UI (for loading data, or using a cursor to look around.. or a way to close the program without using alt+tab or Windows+D). 
<img src = "http://teamjimroberts.com/wp-content/uploads/2016/02/readme_neuron2.png"/>
