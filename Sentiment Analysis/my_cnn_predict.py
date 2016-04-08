from nltk.tokenize import WordPunctTokenizer
import os
from flask import Flask
from flask import request, Response

app = Flask(__name__)

def make_string(ip):
	acc = ""
	for i in ip:
		acc = acc+" "+i.lower()
	return acc.strip()

@app.route('/', methods=['POST'])
def senti_values():
	dict_json = request.get_json(force=True)
	s = dict_json['input'] 
	tokens = []
	tokenizer=WordPunctTokenizer()
	tokens.extend(tokenizer.tokenize(s))
	print tokens
	op = make_string(tokens)
	print op

	# File names
	input_prefix = 'test'
	input_values = 'test.tok'
	input_labels = 'test.cat'
	vocab_file = 'imdb_trn-30000.vocab'
	label_dictionary_file = 'imdb_cat.dic'
	output_prefix = 'test-op'
	model_file = 'imdb-seq.mod.ite100'
	pred_file = 'test.pred.txt'

	# Make the input values file
	f = open(input_values, 'wb')
	f.write(op)
	f.close()

	# Make the input labels file
	f = open(input_labels, 'wb')
	f.write('pos')
	f.close()

	# Generate regions for input sentence
	cmd_gen_regions = './prepText gen_regions input_fn={} text_fn_ext=.tok label_fn_ext=.cat vocab_fn={} label_dic_fn={} patch_size=3 region_fn_stem={} | tee {}'.format(input_prefix, vocab_file, label_dictionary_file, output_prefix, 'cmd_gen_regions.log')
	os.chdir('/home/venky/My/')
	os.system(cmd_gen_regions)

	# Predict the data
	cmd_cnn_predict = './conText -1 cnn_predict model_fn={} prediction_fn={} WriteText datatype=sparse tstname={} x_ext=.xsmatvar | tee {}'.format(model_file, pred_file, output_prefix, 'cmd_cnn_predict.log')
	os.chdir('/home/venky/My/')
	os.system(cmd_cnn_predict)

	# Read Prediction values
	f = open(pred_file,'r')
	val = f.readline()
	print val
	return val, 200
	
if __name__ == '__main__':
    app.run(host='219.91.153.213', port=5000, debug=True, threaded=True)



'''
../bin/prepText gen_regions input_fn=data/new text_fn_ext=.tok label_fn_ext=.cat vocab_fn=data/imdb_trn-30000.vocab label_dic_fn=data/imdb_cat.dic patch_size=3 region_fn_stem=data/new-test

../bin/conText -1 cnn_predict model_fn=output/imdb-seq.mod.ite100 prediction_fn=output/new_test.pred.txt WriteText datatype=sparse tstname=new-test x_ext=.xsmatvar data_dir=data
'''
