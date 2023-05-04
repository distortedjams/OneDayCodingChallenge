CXX = g++
CXXFLAGS = -Wall -O2
LIBS = `pkg-config --libs opencv4`
INCLUDES = `pkg-config --cflags opencv4`

SRCS = main.cpp
OBJS = $(SRCS:.cpp=.o)

TARGET = main

.PHONY: all clean

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) $(INCLUDES) -o $@ $^ $(LIBS)

%.o: %.cpp
	$(CXX) $(CXXFLAGS) $(INCLUDES) -c -o $@ $<

clean:
	rm -f $(TARGET) $(OBJS)
