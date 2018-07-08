#include <fstream>
#include <iomanip>
#include <unordered_map>
#include <string>
#include <list>
#include <sstream>
#include <iostream>
#include <cmath>
#include <time.h>

using namespace std;

const int wait = 5 * 60;
const double overlapR = 0.9;
const double overlapA = 0.05;
string csvLine(istream & is);
string getID(const string& line, int col);
time_t StringToDatetime(const char *str);

struct Order
{
	Order() {}
	Order(time_t s, double fo, double fa, double to, double ta, int p) :
		start(s), from_lo(fo), from_la(fa), to_lo(to), to_la(ta), passengers(p),
		distance(sqrt(pow(fo - to, 2) + pow(fa - ta, 2))) {}
	time_t start;
	double from_lo;
	double from_la;
	double to_lo;
	double to_la;
	double distance;
	int passengers;
};

int main() {
	long sum = 0;
	long success = 0;
	double roadB = 0;
	double roadA = 0;
	string line;
	time_t lastT = NULL;
	time_t nowT = NULL;
	Order order;
	list<Order> orders;
	list<Order>::iterator ite;
	ifstream ifs("train-sort.csv");
	line = csvLine(ifs);
	while (ifs && ifs.peek() != EOF) {
		sum++;
		line = csvLine(ifs);
		order = Order(StringToDatetime(getID(line, 3).data()), atof(getID(line, 6).data()),
			atof(getID(line, 7).data()), atof(getID(line, 8).data()), atof(getID(line, 9).data()),
			atoi(getID(line, 5).data()));
		nowT = order.start;

		if (order.passengers >= 4) {
			roadB += 2 * order.distance;
			roadA += order.distance;
		}
		else
		{
			roadB += order.distance;
		}

		if (order.passengers % 4 == 0) {
			continue;
		}
		else {
			order.passengers = order.passengers % 4;
		}

		if (lastT == NULL) {
			lastT = nowT;
		}

		//clean timeout orders
		if (difftime(nowT, lastT) > wait) {
			for (ite = orders.begin(); ite != orders.end();) {
				if (difftime(nowT, ite->start) > wait) {
					roadA += ite->distance;
					ite = orders.erase(ite);
				}
				else
				{
					lastT = ite->start;
					break;
				}
			}
		}

		//seek for sharing
		bool flag = false;
		double first = -1;
		double last = -1;
		double m11 = -1;
		double m22 = -1;
		double m12 = -1;
		double m21 = -1;
		double middle = -1;
		double cost = -1;
		for (ite = orders.begin(); ite != orders.end(); ite++) {
			if (order.passengers + ite->passengers > 4)
				continue;

			first = sqrt(pow(order.from_lo - ite->from_lo, 2) + pow(order.from_la - ite->from_la, 2));
			last = sqrt(pow(order.to_lo - ite->to_lo, 2) + pow(order.to_la - ite->to_la, 2));
			m11 = order.distance;
			m22 = ite->distance;
			m12 = sqrt(pow(order.from_lo - ite->to_lo, 2) + pow(order.from_la - ite->to_la, 2));
			m21 = sqrt(pow(ite->from_lo - order.to_lo, 2) + pow(order.from_la - ite->to_la, 2));

			middle = m11 > m22 ? m22 : m11;
			middle = middle > m12 ? m12 : middle;
			middle = middle > m21 ? m21 : middle;

			if (middle == m11) {
				cost = first + middle + last;
				if ((cost * overlapR < ite->distance) && (cost - ite->distance < overlapA)) {
					flag = true;
				}
			}

			if (middle == m22) {
				cost = first + middle + last;
				if ((cost * overlapR < order.distance) && (cost - order.distance < overlapA)) {
					flag = true;
				}
			}

			if (middle == m12) {
				cost = first + middle;
				if ((cost * overlapR < ite->distance) && (cost - ite->distance < overlapA)) {
					cost = middle + last;
					if ((cost * overlapR < order.distance) && (cost - order.distance < overlapA)) {
						flag = true;
					}
				}
			}

			if (middle == m21) {
				cost = first + middle;
				if ((cost * overlapR < order.distance) && (cost - order.distance < overlapA)) {
					cost = middle + last;
					if ((cost * overlapR < ite->distance) && (cost - ite->distance < overlapA)) {
						flag = true;
					}
				}
			}

			if (flag) {
				success++;
				roadA += cost;
				orders.erase(ite);
				break;
			}
		}

		if (flag == false) {
			orders.push_back(order);
		}

	}
	cout << "roadB: " << roadB << endl;
	cout << "roadA: " << roadA << endl;
	cout << "success: " << success << endl;
	cout << "sum: " << sum << endl;
	return 0;
}

time_t StringToDatetime(const char *str)
{
	tm tm_;
	int year, month, day, hour, minute, second;
	sscanf(str, "%d-%d-%d %d:%d:%d", &year, &month, &day, &hour, &minute, &second);
	tm_.tm_year = year - 1900;
	tm_.tm_mon = month - 1;
	tm_.tm_mday = day;
	tm_.tm_hour = hour;
	tm_.tm_min = minute;
	tm_.tm_sec = second;
	tm_.tm_isdst = 0;

	time_t t_ = mktime(&tm_); //已经减了8个时区
	return t_; //秒时间
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
