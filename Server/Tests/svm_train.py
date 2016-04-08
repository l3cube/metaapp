from svmutil import *

train_values_file = 'imdb-train.txt.tok'
train_labels_file = 'imdb-train.cat'
test_values_file = 'imdb-test.txt.tok'
test_labels_file = 'imdb-test.cat'
vocab_file = 'imdb_trn-30000.vocab'


def generate_vocab():
    with open(vocab_file) as f:
        lst = f.readlines()
        lst = [l.strip().split(" ")[0] for l in lst]
        return lst


def transform_values(x):
    op = [0] * vocab_size
    for item in x:
        try:
            ind = vocab_list.index(item)
            op[ind] = 1
        except ValueError:
            continue
    return op


def transform_labels(x):
    if x == 'pos':
        return 1
    else:
        return 0

# load vocabulary file
vocab_list = generate_vocab()
vocab_size = len(vocab_list)

# load training data
with open(train_values_file) as f:
    content = f.readlines()
content = [l.strip().split(" ") for l in content]
train_values = map(transform_values, content)
with open(train_labels_file) as f:
    content = f.readlines()
content = [l.strip() for l in content]
train_labels = map(transform_labels, content)

# load test data
with open(test_values_file) as f:
    content = f.readlines()
content = [l.strip().split(" ") for l in content]
test_values = map(transform_values, content)
with open(test_labels_file) as f:
    content = f.readlines()
content = [l.strip() for l in content]
test_labels = map(transform_labels, content)

# SVM training
prob = svm_problem(train_labels, train_values)
param = svm_parameter('')
model = svm_train(prob, param)

# SVM testing
p_labs, p_acc, p_vals = svm_predict(test_labels, test_values, model, '')
print 'Test accuracy: '+p_acc

# Save model
svm_save_model('svm_model', model)

# Prediction using the saved svm model
'''
m = svm_load_model('svm_model')
x = 'New sentence'
x = [transform_values(x.strip().split(" "))]
p_labs, p_acc, p_vals = svm_predict([0]*len(x), x, m, '')
print 'Predicted label: '+p_labs
'''