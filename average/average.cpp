#include <fstream>
#include <iomanip>
#include <unordered_map>
#include <string>
#include <sstream>
#include <iostream>

using namespace std;

string csvLine(istream & is);
string getID(const string& line, int col);

int main() {
	long lines = 0;
	long sum = 0;
	long num = 0;
	int carry = 0;
	string line;
	ifstream ifs("train.csv");
	while (ifs && ifs.peek() != EOF) {
		lines++;
		line = csvLine(ifs);
		carry = atoi(getID(line, 5).data());
		switch (carry)
		{
		case 1: case 2: case 3: case 4:
			sum += carry;
			num++;
			break;
		case 5: case 6:
			sum += carry;
			num += 2;
			break;
		default:
			break;
		}
	}
	cout << "sum: " << sum << endl;
	cout << "average: " << double(sum) / num << endl;
	cout << "lines:" << lines << endl;
	return 0;
}

string csvLine(istream & is) {
	string line;
	while (is.peek() != '\n') {
		if (is.peek() == '"') {
			line += is.get();
			if (is.peek() != '"') {
				while (true) {
					if (is.peek() == '"') {
						line += is.get();
						if (is.peek() != '"') {
							break;
						}
					}
					line += is.get();
				}
			}
			else line += is.get();
		}
		else line += is.get();
	}
	is.get();
	return line;
}

string getID(const string& line, int col) {
	//bool first = true;
	string id;
	char c;
	stringstream ss(line);
	int last = 1; //start from 1
				  //if (!first) id += ',';
	while (last != col) {
		ss.get(c);
		while (c != ',') {
			if (c == '"') {
				if (ss.peek() == '"') ss.get(c);
				else {
					while (true) {
						ss.get(c);
						if (c == '"') {
							if (ss.peek() == '"') {
								ss.get(c);
							}
							else break;
						}
					}
				}
			}
			ss.get(c);
		}
		last++;
	}
	ss.get(c);
	while (c != ',' && !ss.eof()) {
		if (c == '"') {
			id += c;
			ss.get(c);

			if (c == '"') {
				id += c;
				ss.get(c);
			}

			else {
				while (true) {
					if (c == '"') {
						id += c;
						ss.get(c);
						if (ss.eof()) break;
						if (c == '"') {}
						else {
							break;
						}
					}
					if (c == '\'') id += '\'';
					if (c == '\\') id += '\\';
					id += c;
					ss.get(c);
				}
			}

		}

		else {
			if (c == '\'') id += '\'';
			if (c == '\\') id += '\\';
			id += c;
			ss.get(c);
		}
	}
	last++;
	//if (first) first = false;
	return id;
}
