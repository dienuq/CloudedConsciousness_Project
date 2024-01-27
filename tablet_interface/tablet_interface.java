/* Instructions:
1. Install APDE Android Processing IDE on tablet
2. Create a new sketch with this code
3. save the pics from this folder and modify paths accordingly
4. modify ip to the ip of the PC  
*/


import controlP5.*;
import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress myAddress;

ControlP5 cp5;
PImage img1, img2, shadow, anima, animus, self, persona, parent, child, trickster;

int currentState = 0; // Initialize with the first page 
int archetype = 9;
boolean oscMessageSent = false;

Chart myChart;
Knob myKnob;
Knob myKnob1;
Knob myKnob2;
Knob myKnob3;

int frameCounter = 0;
int updateRate1 = 10;
int updateRate2 = 30;
int updateRate3 = 50;

int color_background = color(28, 8, 50);

Dong[][] d;
int nx = 20;
int ny = 20;


Textarea myTextarea;
Textlabel front_page;
int c = 0;
Println console;


void setup() {
  fullScreen();
  smooth();
  oscP5 = new OscP5(this, 12000); // listening
  myAddress = new NetAddress("xxx", 8887);  //  speak to; replace xxx with ip

  cp5 = new ControlP5(this);
  myChart = cp5.addChart("dataflow")
    .setPosition(30, displayHeight-155)
    .setSize(200, 100)
    .setRange(-20, 20)
    .setView(Chart.LINE) // use Chart.LINE, Chart.PIE, Chart.AREA, Chart.BAR_CENTERED
    .setStrokeWeight(1.5)
    .setColorCaptionLabel(color(255))
    .setColorBackground(color_background) // Change the background color of the chart
    .setColorActive(255)
    ;

  myChart.addDataSet("incoming");
  myChart.setData("incoming", new float[100]);

  cp5 = new ControlP5(this);
  myKnob1 = cp5.addKnob("knob")
    .setRange(0, 255)
    .setValue(80)
    .setPosition(displayWidth-150, (displayHeight/2)-250)
    .setRadius(50)
    .setDragDirection(Knob.VERTICAL)
    .setCaptionLabel("Collecting data...")
    .setColorBackground(color_background)
    .setColorForeground(#2e35aa)
    .setColorActive(#8237b8)
    ;

  cp5 = new ControlP5(this);             
  myKnob2 = cp5.addKnob("knob")
    .setRange(0, 255)
    .setValue(200)
    .setPosition(displayWidth-150, (displayHeight/2)-100)
    .setRadius(50)
    .setDragDirection(Knob.VERTICAL)
    .setCaptionLabel("Analyzing samples...")
    .setColorBackground(color_background)
    .setColorForeground(#2e35aa)
    .setColorActive(#8237b8)
    ;
  cp5 = new ControlP5(this);
  myKnob3 = cp5.addKnob("knob")
    .setRange(0, 255)
    .setValue(50)
    .setPosition(displayWidth-150, (displayHeight/2)+50)
    .setRadius(50)
    .setDragDirection(Knob.VERTICAL)
    .setCaptionLabel("Output prediction...")
    .setColorBackground(color_background)
    .setColorForeground(#2e35aa)
    .setColorActive(#8237b8)

    ; 

  //matrix animation 
  cp5 = new ControlP5(this);

  cp5.addMatrix("myMatrix")
    .setPosition(50, 100)
    .setSize(100, 200)
    .setGrid(nx, ny)
    .setGap(1, 1)
    .setInterval(500)
    .setMode(ControlP5.MULTIPLES)
    .setColorBackground(color_background)
    .setBackground(#2e35aa)
    .setCaptionLabel(" ")
    ;

  cp5.getController("myMatrix").getCaptionLabel().alignX(CENTER);
  d = new Dong[nx][ny];
  for (int x = 0; x<nx; x++) {
    for (int y = 0; y<ny; y++) {
      d[x][y] = new Dong();
    }
  }  

  cp5 = new ControlP5(this);
  cp5.enableShortcuts();
  frameRate(120);
  myTextarea = cp5.addTextarea("txt")
    .setPosition(30, 390)
    .setSize(70, 150)
    .setFont(createFont("Courier New", 10))
    .setLineHeight(14)
    .setColor(color(200))
    .setColorBackground(color_background)
    .setColorForeground(#2e35aa)
    .setColorActive(#8237b8)
    .setWidth(150)
    ;                  
  console = cp5.addConsole(myTextarea);

  // create new buttons
  cp5.addButton("Why am I here?")
    .setId(1)
    .setValue(0)
    .setPosition(displayWidth-250, displayHeight-125)
    .setSize(200, 50)
    .setColorBackground(#2e35aa)
    .setCaptionLabel("")
    ;

  front_page = cp5.addTextlabel("label")
    .setPosition(displayWidth-250, 150)
    .setColorValue(255)
    .setFont(createFont("Courier New", 40))
    .setText("START")
    ;
  img1 = loadImage("page2_transparency.png");   //this paths can suffer changes, depending on where the imgs are saved on the tablet
  img2 = loadImage("page3_transparency.png");
  shadow = loadImage("shadow.png");
  anima = loadImage("anima.png");
  animus = loadImage("animus.png");
  self = loadImage("self.png");
  persona = loadImage("persona.png");
  parent = loadImage("parent.png");
  child = loadImage("child.png");
  trickster = loadImage("trickster.png");
}

void draw() { 
  background(color_background);
  draw_page1();

  //knob animation
  if (frameCounter % updateRate1 == 0) {
    float randomValue = random(0, 255);
    smoothKnobTransition(myKnob1, myKnob1.getValue(), randomValue);
  }
  if (frameCounter % updateRate2 == 0) {
    float randomValue = random(0, 255);
    smoothKnobTransition(myKnob2, myKnob2.getValue(), randomValue);
  }
  if (frameCounter % updateRate3 == 0) {
    float randomValue = random(0, 255);
    smoothKnobTransition(myKnob3, myKnob3.getValue(), randomValue);
  }
  frameCounter++;

  //slider 2D animation
  float randomValue = random(-20, 30);
  myChart.push("incoming", randomValue);


  //circle animation
  pushMatrix();
  translate(displayWidth/2, displayHeight/2);
  rotate(frameCount*0.005);
  for (int x = 0; x<nx; x++) {
    for (int y = 0; y<ny; y++) {
      d[x][y].display();
    }
  }
  popMatrix();

  //random list
  float n = sin(frameCount*0.01)*300;
 println(frameCount+"\t"+String.format("%.1f", frameRate)+"\t"+String.format("%.1f", n), frameCount+"\t"+String.format("%.1f", frameRate));

  //switch between different pages
  switch (currentState) {
  case 0: // first page
    cp5.getController("Why am I here?").setVisible(true);
    cp5.getController("label").setVisible(true);
    front_page.setPosition(displayWidth-215, displayHeight-115);
    front_page.setText("START");
    oscMessageSent = false;
    archetype = 9;
    draw_page1();
    break;

  case 1: // synopsis

    front_page.setPosition(displayWidth-205, displayHeight-115);
    front_page.setText("NEXT");
    draw_page2();

    break;

  case 2: // intructions -> waiting for writing

    front_page.setPosition(displayWidth-225, displayHeight-115);
    front_page.setText("Analyze");

    draw_page3();
    break;

  case 3: // Output -> archetype
    cp5.getController("Why am I here?").setVisible(false);
    cp5.getController("label").setVisible(false);
    if (!oscMessageSent) { // Check if the OSC message hasn't been sent yet
      OscMessage myMessage = new OscMessage("/analyze");
      /* send the message */
      oscP5.send(myMessage, myAddress);
      oscMessageSent = true; // Mark the message as sent
    }
    draw_page4();
    break;
  }
}

void draw_page1() {
  
}

void draw_page2() {
  background(color_background);


  image(img1, displayWidth/2-220, displayHeight/2-230);
}

void draw_page3() {
  background(color_background);
  image(img2, displayWidth/2-220, displayHeight/2-230);
}

void draw_page4() {
  background(color_background);

  switch (archetype) {
  case 0: // shadow
    image(shadow, displayWidth/2-200, displayHeight/2-340);
    break;
  case 1: // anima
    image(anima, displayWidth/2-200, displayHeight/2-340);
    break;
  case 2: // animus
    image(animus, displayWidth/2-200, displayHeight/2-340);
    break;
  case 3: // self
    image(self, displayWidth/2-200, displayHeight/2-340);
    break; 
  case 4: // persona
    image(persona, displayWidth/2-200, displayHeight/2-340);
    break;
  case 5: // parent
    image(parent, displayWidth/2-200, displayHeight/2-340);
    break;
  case 6: // child
    image(child, displayWidth/2-200, displayHeight/2-340);
    break;
  case 7: // trickster
    image(trickster, displayWidth/2-200, displayHeight/2-340);
    break;
  }
}

void smoothKnobTransition(Knob knob, float currentValue, float targetValue) {
  int transitionFrames = 50; // Number of frames for smooth transition
  float increment = (targetValue - currentValue) / transitionFrames;
  float newValue = currentValue + 5*increment;
  knob.setValue(newValue);
}

void myMatrix(int theX, int theY) {
  d[theX][theY].update();
}

class Dong {
  float x, y, x2, y2;
  float s0, s1;

  Dong() {
    float f= random(-PI, PI);
    x = cos(f)*random(400, 420);
    y = sin(f)*random(400, 420);
    x2 = cos(f)*random(300, 350);
    y2 = sin(f)*random(300, 350);
    s0 = random(2, 10);
  }

  void display() {
    s1 += (s0-s1)*0.1;
    ellipse(x, y, s1, s1);
    ellipse(x2, y2, s1, s1);
  }

  void update() {
    s1 = 50;
  }

  void reset() {
    s1 = 0; // Reset animation state to initial value
  }
}

void controlEvent(ControlEvent theEvent) {
  String eventName = theEvent.getName();
  int buttonId = theEvent.getId();

  // Check if the event is coming from a button and if it's not the initial setup event
  if (theEvent.isController() && millis() > 1500) {
    if (eventName.equals("Why am I here?")) {
      currentState++; // Move to the next state

      if (currentState > 3) {
        currentState = 0; // Reset to the first page
      }

      println("Button value: " + theEvent.getController().getValue());
      println("Button ID: " + buttonId); // Print the button's ID
      println("Current state: " + currentState);
    }
  }
}

void oscEvent(OscMessage msg) {
  String address = msg.addrPattern();
  println(" addrpattern: " + msg.addrPattern());
  
  if (address.equals("/mandala/0")) {
    archetype = 0; //shadow
  } else if (address.equals("/mandala/1")) {
    archetype = 1; //anima
  } else if (address.equals("/mandala/2")) {
    archetype = 2; //animus
  } else if (address.equals("/mandala/3")) {
    archetype = 3; //self
  } else if (address.equals("/mandala/4")) {
    archetype = 4; //persona
  } else if (address.equals("/mandala/5")) {
    archetype = 5; //parent
  } else if (address.equals("/mandala/6")) {
    archetype = 6; //child
  } else if (address.equals("/mandala/7")) {
    archetype = 7; //trickster
  } else if (address.equals("/reset")) {
    currentState = 0; //go to the first page
  }
}