#include <opencv2/opencv.hpp>
#include <iostream>
#include <string>

int main(int argc, char** argv) {
    if (argc < 3) {
        std::cout << "Usage: " << argv[0] << " <input_file> [-r <width>x<height>]" << std::endl;
        return -1;
    }

    cv::Mat image;
    image = cv::imread(argv[1]); // load image from file

    if (!image.data) {
        std::cout << "Could not open or find the image" << std::endl;
        return -1;
    }

    cv::Mat processed_image;

    std::string output_file;
    for (int i = 2; i < argc;) {
        std::string arg = argv[i];

        if (arg == "-r") { // resize image
            if (i + 1 < argc) {
                std::string size = argv[i + 1];
                size_t x_pos = size.find("x");

                if (x_pos != std::string::npos) {
                    int width = std::stoi(size.substr(0, x_pos));
                    int height = std::stoi(size.substr(x_pos + 1));
                    cv::resize(image, processed_image, cv::Size(width, height));
                } else {
                    std::cout << "Invalid size argument" << std::endl;
                    return -1;
                }

                i += 2;
                if (i < argc && argv[i][0] != '-') {
                    output_file = argv[i];
                    i++;
                }
            } else {
                std::cout << "Size argument missing" << std::endl;
                return -1;
            }
        } else {
            i++; // only increment if current argument is not a flag
        }
    }

    cv::imshow("Input Image", image);
    cv::imshow("Processed Image", processed_image);
    cv::waitKey(0);

    return 0;
}
