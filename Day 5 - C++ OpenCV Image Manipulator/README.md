Image Processing Program
This is a simple command-line program that allows you to process images. It currently supports resizing an image.

Requirements
OpenCV 2 or later
C++11 or later

Usage
./main <input_file> [-r <width>x<height>] [output_file]

<input_file>: The path to the input image file.
-r <width>x<height>: Resizes the image to the specified width and height.
[output_file]: (optional) The path to the output image file. If not specified, the program will display the processed image.

Examples
Resize an image:
./main cat.jpg -r 300x300

Resize an image and save the output to a file:
./main cat.jpg -r 300x300 output.jpg

Limitations
Only supports resizing images.
Only supports JPEG, PNG, and BMP image formats.
