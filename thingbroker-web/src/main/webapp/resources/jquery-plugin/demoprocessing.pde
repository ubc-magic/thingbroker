void setup() {
    size(400,200);
    noLoop();
    stroke(#FFEE88);
    fill(#FFEE88);
    background(#000033);
    text("",0,0);
    textSize(24);
}

void draw() { } //Skip this for the demo

void drawTasks(HashMap data[]){
   background(#000033);
   t =  data[data.length-1].info.task; //first event
   t = data[0].info.task;
   int r = data.length;
   float twidth = textWidth(t);
   text("task: "+t, (width - twidth)/4, height/2);
   ellipse( width/2, height-(height/4), r,r);
   textSize(10);
   text("Circle width represents number of tasks submitted", 10, 10);
}


